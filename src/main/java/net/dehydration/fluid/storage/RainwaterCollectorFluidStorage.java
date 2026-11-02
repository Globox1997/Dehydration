package net.dehydration.fluid.storage;

import com.google.common.primitives.Ints;
import net.dehydration.init.BlockInit;
import net.dehydration.init.FluidInit;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class RainwaterCollectorFluidStorage extends SnapshotParticipant<BlockState> implements SingleSlotStorage<FluidVariant> {

    private final Level world;
    private final BlockPos pos;

    private BlockState lastReleasedSnapshot;

    public RainwaterCollectorFluidStorage(Level world, BlockPos pos) {
        this.world = world;
        this.pos = pos;
    }

    private RainwaterCollectorFluidContent getCurrentContent() {
        return new RainwaterCollectorFluidContent(createSnapshot().getBlock());
    }

    private void updateLevel(RainwaterCollectorFluidContent newContent, int level, TransactionContext transaction) {
        updateSnapshots(transaction);
        BlockState newState = newContent.block.defaultBlockState();

        if (newContent.levelProperty != null) {
            newState = newState.setValue(newContent.levelProperty, level);
        }

        world.setBlock(pos, newState, 0);
    }

    @Override
    public long insert(FluidVariant fluidVariant, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(fluidVariant, maxAmount);

        if (!fluidVariant.isOf(Fluids.WATER) && !fluidVariant.isOf(FluidInit.PURIFIED_WATER)) {
            return 0;
        }

        RainwaterCollectorFluidContent insertContent = new RainwaterCollectorFluidContent(fluidVariant.getFluid());

        int maxLevelsInserted = Ints.saturatedCast(maxAmount / insertContent.amountPerLevel);

        if (getAmount() == 0) {
            int levelsInserted = Math.min(maxLevelsInserted, insertContent.maxLevel);

            if (levelsInserted > 0) {
                updateLevel(insertContent, levelsInserted, transaction);
            }

            return levelsInserted * insertContent.amountPerLevel;
        }

        RainwaterCollectorFluidContent currentContent = getCurrentContent();
        RainwaterCollectorFluidContent mixedContent;

        if (fluidVariant.isOf(currentContent.fluid)) {
            mixedContent = currentContent;
        } else if (fluidVariant.isOf(Fluids.WATER) || fluidVariant.isOf(FluidInit.PURIFIED_WATER)) {
            mixedContent = new RainwaterCollectorFluidContent(Fluids.WATER);
        } else {
            return 0;
        }

        int currentLevel = currentContent.currentLevel(createSnapshot());
        int levelsInserted = Math.min(maxLevelsInserted, currentContent.maxLevel - currentLevel);

        if (levelsInserted > 0) {
            updateLevel(mixedContent, currentLevel + levelsInserted, transaction);
        }

        return levelsInserted * mixedContent.amountPerLevel;
    }

    @Override
    public long extract(FluidVariant fluidVariant, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(fluidVariant, maxAmount);

        RainwaterCollectorFluidContent currentContent = getCurrentContent();

        if (fluidVariant.isOf(currentContent.fluid)) {
            int maxLevelsExtracted = Ints.saturatedCast(maxAmount / currentContent.amountPerLevel);
            int currentLevel = currentContent.currentLevel(createSnapshot());
            int levelsExtracted = Math.min(maxLevelsExtracted, currentLevel);

            if (levelsExtracted > 0) {
                if (levelsExtracted == currentLevel) {
                    updateSnapshots(transaction);
                    world.setBlock(pos, BlockInit.RAINWATER_COLLECTOR_BLOCK.defaultBlockState(), 0);
                } else {
                    updateLevel(currentContent, currentLevel - levelsExtracted, transaction);
                }
            }

            return levelsExtracted * currentContent.amountPerLevel;
        }

        return 0;
    }

    @Override
    public boolean isResourceBlank() {
        return getResource().isBlank();
    }

    @Override
    public FluidVariant getResource() {
        return FluidVariant.of(getCurrentContent().fluid);
    }

    @Override
    public long getAmount() {
        RainwaterCollectorFluidContent currentContent = getCurrentContent();
        return currentContent.currentLevel(createSnapshot()) * currentContent.amountPerLevel;
    }

    @Override
    public long getCapacity() {
        RainwaterCollectorFluidContent currentContent = getCurrentContent();
        return currentContent.maxLevel * currentContent.amountPerLevel;
    }

    @Override
    public BlockState createSnapshot() {
        return world.getBlockState(pos);
    }

    @Override
    public void readSnapshot(BlockState savedState) {
        world.setBlock(pos, savedState, 0);
    }

    @Override
    protected void releaseSnapshot(BlockState snapshot) {
        lastReleasedSnapshot = snapshot;
    }

    @Override
    public void onFinalCommit() {
        BlockState state = createSnapshot();
        BlockState originalState = lastReleasedSnapshot;

        if (originalState != state) {
            world.setBlock(pos, originalState, 0);
            world.setBlockAndUpdate(pos, state);
        }
    }

    @Override
    public String toString() {
        return "RainwaterCollectorStorage[" + world + ", " + pos + "]";
    }

    private static class RainwaterCollectorFluidContent {

        final Block block;
        final Fluid fluid;
        final long amountPerLevel;
        final int maxLevel;
        final IntegerProperty levelProperty;

        private RainwaterCollectorFluidContent(Block block) {
            if (block == BlockInit.RAINWATER_WATER_COLLECTOR_BLOCK) {
                this.block = block;
                fluid = Fluids.WATER;
                amountPerLevel = FluidConstants.BOTTLE;
                maxLevel = 3;
                levelProperty = LayeredCauldronBlock.LEVEL;
            } else if (block == BlockInit.RAINWATER_PURIFIED_WATER_COLLECTOR_BLOCK) {
                this.block = block;
                fluid = FluidInit.PURIFIED_WATER;
                amountPerLevel = FluidConstants.BOTTLE;
                maxLevel = 3;
                levelProperty = LayeredCauldronBlock.LEVEL;
            } else {
                this.block = BlockInit.RAINWATER_COLLECTOR_BLOCK;
                fluid = Fluids.EMPTY;
                amountPerLevel = FluidConstants.BUCKET;
                maxLevel = 1;
                levelProperty = null;
            }
        }

        private RainwaterCollectorFluidContent(Fluid fluid) {
            if (fluid == Fluids.WATER) {
                this.fluid = fluid;
                block = BlockInit.RAINWATER_WATER_COLLECTOR_BLOCK;
                amountPerLevel = FluidConstants.BOTTLE;
                maxLevel = 3;
                levelProperty = LayeredCauldronBlock.LEVEL;
            } else if (fluid == FluidInit.PURIFIED_WATER) {
                this.fluid = fluid;
                block = BlockInit.RAINWATER_PURIFIED_WATER_COLLECTOR_BLOCK;
                amountPerLevel = FluidConstants.BOTTLE;
                maxLevel = 3;
                levelProperty = LayeredCauldronBlock.LEVEL;
            } else {
                this.fluid = Fluids.EMPTY;
                block = BlockInit.RAINWATER_COLLECTOR_BLOCK;
                amountPerLevel = FluidConstants.BUCKET;
                maxLevel = 1;
                levelProperty = null;
            }
        }

        private int currentLevel(BlockState state) {
            if (fluid == Fluids.EMPTY) {
                return 0;
            } else if (levelProperty == null) {
                return 1;
            } else {
                return state.getValue(levelProperty);
            }
        }

    }
}
