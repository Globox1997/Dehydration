package net.dehydration.block.entity;

import net.dehydration.block.CampfireCauldronBlock;
import net.dehydration.init.BlockInit;
import net.dehydration.init.ConfigInit;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;

public class CampfireCauldronEntity extends BlockEntity implements Tickable {

    public int ticker;
    public boolean isBoiled;

    public CampfireCauldronEntity() {
        super(BlockInit.CAMPFIRE_CAULDRON_ENTITY);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        isBoiled = tag.getBoolean("Boiled");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putBoolean("Boiled", isBoiled);
        return tag;
    }

    @Override
    public void tick() {
        CampfireCauldronBlock campfireCauldronBlock = (CampfireCauldronBlock) this.getCachedState().getBlock();
        if (campfireCauldronBlock.isFireBurning(world, pos)
                && this.getCachedState().get(CampfireCauldronBlock.LEVEL) > 0 && !isBoiled) {
            ticker++;
            if (ticker >= ConfigInit.CONFIG.water_boiling_time) {
                isBoiled = true;
                ticker = 0;
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

}
