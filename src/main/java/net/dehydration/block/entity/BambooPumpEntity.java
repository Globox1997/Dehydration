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
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BambooPumpEntity extends BlockEntity implements Inventory {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private int pumpCount = 0;
    private int cooldown = 0;

    public BambooPumpEntity(BlockPos pos, BlockState state) {
        super(BlockInit.BAMBOO_PUMP_ENTITY, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt, WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.inventory.clear();
        Inventories.readNbt(nbt, inventory, registryLookup);
    }

    @Override
    public void writeNbt(NbtCompound nbt, WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        sendUpdate();
    }

    public static void clientTick(World world, BlockPos pos, BlockState state, BambooPumpEntity blockEntity) {
        blockEntity.update();
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, BambooPumpEntity blockEntity) {
        blockEntity.update();
    }

    private void update() {
        if (this.cooldown > 0)
            this.cooldown--;
    }

    private void sendUpdate() {
        if (this.world != null) {
            BlockState state = this.world.getBlockState(this.pos);
            (this.world).updateListeners(this.pos, state, state, 3);
        }
    }

    private void updateInventory() {
        ItemStack itemStack = getStack(0);
        if (!itemStack.isEmpty()) {
            Storage<FluidVariant> fluidStorage = null;
            Storage<ItemVariant> itemStorage = ItemStorage.SIDED.find(this.world, this.pos, null);
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
                    }
                }
            }
        }
    }

    @Override
    public void clear() {
        this.inventory.clear();
        this.pumpCount = 0;
        this.markDirty();
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return this.getStack(0).isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.inventory.get(0);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack result = Inventories.splitStack(this.inventory, slot, 1);
        if (!result.isEmpty()) {
            markDirty();
        }
        return result;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.clear();
        this.inventory.set(0, stack);
        this.markDirty();
    }

    @Override
    public ItemStack removeStack(int slot) {
        this.markDirty();
        return Inventories.removeStack(this.inventory, slot);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(WrapperLookup registryLookup) {
        return this.createNbt(registryLookup);
    }

    public void increasePumpCount(int count) {
        this.pumpCount += count;
        updateInventory();
    }

    public int getCooldown() {
        return this.cooldown;
    }

    public void setCooldown(int ticks) {
        this.cooldown = ticks;
    }

}
