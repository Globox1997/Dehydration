package net.dehydration.block.entity;

import net.dehydration.init.BlockInit;
import net.dehydration.init.ConfigInit;
import net.dehydration.init.ItemInit;
import net.dehydration.item.LeatherFlask;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BambooPumpEntity extends BlockEntity implements Inventory {

    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private int pumpCount = 0;
    private int cooldown = 0;

    public BambooPumpEntity(BlockPos pos, BlockState state) {
        super(BlockInit.BAMBOO_PUMP_ENTITY, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory.clear();
        Inventories.readNbt(nbt, inventory);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
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
        if (!getStack(0).isEmpty()) {
            ItemStack itemStack = getStack(0);
            if (itemStack.isOf(Items.BUCKET)) {
                if (pumpCount > 3) {
                    if (!this.world.isClient())
                        setStack(0, new ItemStack(ItemInit.PURIFIED_BUCKET));
                    pumpCount = 0;
                    cooldown = ConfigInit.CONFIG.pump_cooldown;
                }
            } else if (itemStack.isOf(Items.GLASS_BOTTLE)) {
                if (!this.world.isClient())
                    setStack(0, PotionUtil.setPotion(new ItemStack(Items.POTION), ItemInit.PURIFIED_WATER));
                pumpCount = 0;
                cooldown = ConfigInit.CONFIG.pump_cooldown;
            } else if (itemStack.getItem() instanceof LeatherFlask) {
                if (!this.world.isClient()) {
                    LeatherFlask.fillFlask(itemStack, 2);
                    setStack(0, itemStack);
                }
                pumpCount = 0;
                cooldown = ConfigInit.CONFIG.pump_cooldown;
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
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
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
