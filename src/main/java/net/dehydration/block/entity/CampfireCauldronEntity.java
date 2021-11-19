package net.dehydration.block.entity;

import net.dehydration.block.CampfireCauldronBlock;
import net.dehydration.init.BlockInit;
import net.dehydration.init.ConfigInit;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CampfireCauldronEntity extends BlockEntity {
    public boolean isBoiled;
    private int ticker;

    public CampfireCauldronEntity(BlockPos pos, BlockState state) {
        super(BlockInit.CAMPFIRE_CAULDRON_ENTITY, pos, state);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        this.isBoiled = tag.getBoolean("Boiled");
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putBoolean("Boiled", isBoiled);
    }

    public static void clientTick(World world, BlockPos pos, BlockState state, CampfireCauldronEntity blockEntity) {
        blockEntity.update();
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, CampfireCauldronEntity blockEntity) {
        blockEntity.update();
    }

    public void update() {
        CampfireCauldronBlock campfireCauldronBlock = (CampfireCauldronBlock) this.getCachedState().getBlock();
        if (campfireCauldronBlock.isFireBurning(world, pos) && this.getCachedState().get(CampfireCauldronBlock.LEVEL) > 0 && !this.isBoiled) {
            this.ticker++;
            if (this.ticker >= ConfigInit.CONFIG.water_boiling_time) {
                this.isBoiled = true;
                this.ticker = 0;
            }
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
        sendUpdate();
    }

    private void sendUpdate() {
        if (this.world != null) {
            BlockState state = this.world.getBlockState(this.pos);
            (this.world).updateListeners(this.pos, state, state, 3);
        }
    }

    // Client sync issue since method gets only called on server and client looks for isBoiled
    public void onFillingCauldron() {
        this.isBoiled = false;
        this.ticker = 0;
    }

}
