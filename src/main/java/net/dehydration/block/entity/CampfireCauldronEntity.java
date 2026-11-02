package net.dehydration.block.entity;

import net.dehydration.block.CampfireCauldronBlock;
import net.dehydration.init.BlockInit;
import net.dehydration.init.ConfigInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CampfireCauldronEntity extends BlockEntity {
    public boolean isBoiled;
    private int ticker;

    public CampfireCauldronEntity(BlockPos pos, BlockState state) {
        super(BlockInit.CAMPFIRE_CAULDRON_ENTITY, pos, state);
    }

    @Override
    public void loadAdditional(CompoundTag tag, Provider registryLookup) {
        super.loadAdditional(tag, registryLookup);
        this.isBoiled = tag.getBoolean("Boiled");
        this.ticker = tag.getInt("BoilingTicks");
        if (this.level != null && this.level.isClientSide()) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag, Provider registryLookup) {
        super.saveAdditional(tag, registryLookup);
        tag.putBoolean("Boiled", isBoiled);
        tag.putInt("BoilingTicks", ticker);
    }

    public static void serverTick(Level world, BlockPos pos, BlockState state, CampfireCauldronEntity blockEntity) {
        blockEntity.update();
    }

    public void update() {
        if (this.level == null) {
            return;
        }
        if (this.getBlockState().getValue(CampfireCauldronBlock.LEVEL) <= 0) {
            if (this.isBoiled || this.ticker != 0) {
                this.isBoiled = false;
                this.ticker = 0;
                this.setChanged();
            }
            return;
        }
        CampfireCauldronBlock campfireCauldronBlock = (CampfireCauldronBlock) this.getBlockState().getBlock();
        if (!this.isBoiled && campfireCauldronBlock.isFireBurning(level, worldPosition)) {
            this.ticker++;
            if (this.ticker >= ConfigInit.CONFIG.water_boiling_time) {
                this.isBoiled = true;
                this.ticker = 0;
                this.setChanged();
            }
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
        sendUpdate();
    }

    private void sendUpdate() {
        if (this.level != null) {
            BlockState state = this.level.getBlockState(this.worldPosition);
            (this.level).sendBlockUpdated(this.worldPosition, state, state, 3);
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(Provider registryLookup) {
        return this.saveWithoutMetadata(registryLookup);
    }

    public void onFillingCauldron() {
        this.isBoiled = false;
        this.ticker = 0;
        this.setChanged();
    }

}
