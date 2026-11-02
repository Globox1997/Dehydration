package net.dehydration.block.entity;

import net.dehydration.init.BlockInit;
import net.dehydration.init.ConfigInit;
import net.dehydration.init.FluidInit;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class BambooPumpEntity extends BlockEntity implements Container {

    private final NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);
    private int pumpCount = 0;
    private int cooldown = 0;

    public BambooPumpEntity(BlockPos pos, BlockState state) {
        super(BlockInit.BAMBOO_PUMP_ENTITY, pos, state);
    }

    @Override
    public void loadAdditional(ValueInput nbt) {
        super.loadAdditional(nbt);
        this.inventory.clear();
        ContainerHelper.loadAllItems(nbt, inventory);
        this.pumpCount = nbt.getIntOr("PumpCount", 0);
        this.cooldown = nbt.getIntOr("Cooldown", 0);
    }

    @Override
    public void saveAdditional(ValueOutput nbt) {
        super.saveAdditional(nbt);
        ContainerHelper.saveAllItems(nbt, inventory);
        nbt.putInt("PumpCount", this.pumpCount);
        nbt.putInt("Cooldown", this.cooldown);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        sendUpdate();
    }

    public static void clientTick(Level world, BlockPos pos, BlockState state, BambooPumpEntity blockEntity) {
        blockEntity.update();
    }

    public static void serverTick(Level world, BlockPos pos, BlockState state, BambooPumpEntity blockEntity) {
        blockEntity.update();
    }

    private void update() {
        if (this.cooldown > 0) {
            this.cooldown--;
            if (this.cooldown == 0 && this.level != null && !this.level.isClientSide()) {
                this.setChanged();
            }
        }
    }

    private void sendUpdate() {
        if (this.level != null) {
            BlockState state = this.level.getBlockState(this.worldPosition);
            (this.level).sendBlockUpdated(this.worldPosition, state, state, 3);
        }
    }

    private void updateInventory() {
        ItemStack itemStack = getItem(0);
        if (!itemStack.isEmpty()) {
            Storage<FluidVariant> fluidStorage = null;
            Storage<ItemVariant> itemStorage = ItemStorage.SIDED.find(this.level, this.worldPosition, null);
            if (itemStorage instanceof SingleSlotStorage<ItemVariant> singleSlotStorage) {
                fluidStorage = ContainerItemContext.ofSingleSlot(singleSlotStorage).find(FluidStorage.ITEM);
            }
            if (fluidStorage != null && fluidStorage.supportsInsertion()) {
                long amount = pumpCount * FluidConstants.BOTTLE * 2;
                try (Transaction transaction = Transaction.openOuter()) {
                    if (fluidStorage.insert(FluidVariant.of(FluidInit.PURIFIED_WATER), amount, transaction) > 0) {
                        transaction.commit();
                        pumpCount = 0;
                        cooldown = ConfigInit.CONFIG.pump_cooldown;
                        this.setChanged();
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }

    @Override
    public void clearContent() {
        this.inventory.clear();
        this.pumpCount = 0;
        this.setChanged();
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return this.getItem(0).isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.inventory.get(0);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = ContainerHelper.removeItem(this.inventory, slot, 1);
        if (!result.isEmpty()) {
            setChanged();
        }
        return result;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.clearContent();
        this.inventory.set(0, stack);
        this.setChanged();
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        this.setChanged();
        return ContainerHelper.takeItem(this.inventory, slot);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(Provider registryLookup) {
        return this.saveWithoutMetadata(registryLookup);
    }

    public void increasePumpCount(int count) {
        this.pumpCount += count;
        updateInventory();
        super.setChanged();
    }

    public int getCooldown() {
        return this.cooldown;
    }

    public void setCooldown(int ticks) {
        this.cooldown = ticks;
        this.setChanged();
    }

}
