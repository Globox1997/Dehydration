package net.dehydration.neoforge.transfer;

import java.util.Objects;

import net.dehydration.init.FluidInit;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public final class LeveledFluidContainerHandler extends SnapshotJournal<BlockState> implements ResourceHandler<FluidResource> {

    private static final int MAX_LEVEL = 3;

    private final Level world;
    private final BlockPos pos;
    private final Block emptyBlock;
    private final Block waterBlock;
    private final Block purifiedWaterBlock;
    private final IntegerProperty levelProperty;

    public LeveledFluidContainerHandler(Level world, BlockPos pos, Block emptyBlock, Block waterBlock, Block purifiedWaterBlock, IntegerProperty levelProperty) {
        this.world = world;
        this.pos = pos.immutable();
        this.emptyBlock = emptyBlock;
        this.waterBlock = waterBlock;
        this.purifiedWaterBlock = purifiedWaterBlock;
        this.levelProperty = levelProperty;
    }

    private Fluid getFluid(BlockState state) {
        if (state.is(this.waterBlock)) {
            return Fluids.WATER;
        } else if (state.is(this.purifiedWaterBlock)) {
            return FluidInit.PURIFIED_WATER;
        }
        return Fluids.EMPTY;
    }

    private int getLevel(BlockState state) {
        return getFluid(state) == Fluids.EMPTY ? 0 : state.getValue(this.levelProperty);
    }

    private void setLevel(Fluid fluid, int level, TransactionContext transaction) {
        updateSnapshots(transaction);
        BlockState newState;
        if (level <= 0) {
            newState = this.emptyBlock.defaultBlockState();
        } else {
            newState = (fluid == FluidInit.PURIFIED_WATER ? this.purifiedWaterBlock : this.waterBlock).defaultBlockState().setValue(this.levelProperty, level);
        }
        this.world.setBlock(this.pos, newState, 0);
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public FluidResource getResource(int index) {
        Objects.checkIndex(index, size());
        return FluidResource.of(getFluid(this.world.getBlockState(this.pos)));
    }

    @Override
    public long getAmountAsLong(int index) {
        Objects.checkIndex(index, size());
        return (long) getLevel(this.world.getBlockState(this.pos)) * FluidUnits.BOTTLE;
    }

    @Override
    public long getCapacityAsLong(int index, FluidResource resource) {
        Objects.checkIndex(index, size());
        return resource.isEmpty() || FluidUnits.isWater(resource) ? (long) MAX_LEVEL * FluidUnits.BOTTLE : 0;
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        Objects.checkIndex(index, size());
        return FluidUnits.isWater(resource);
    }

    @Override
    public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
        Objects.checkIndex(index, size());
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        if (!FluidUnits.isWater(resource)) {
            return 0;
        }
        BlockState state = this.world.getBlockState(this.pos);
        Fluid currentFluid = getFluid(state);
        int currentLevel = getLevel(state);
        Fluid newFluid = currentFluid == Fluids.EMPTY || resource.getFluid() == currentFluid ? resource.getFluid() : Fluids.WATER;
        FluidUnits.Transfer transfer = FluidUnits.transfer(amount, MAX_LEVEL - currentLevel);
        if (transfer.levels() > 0) {
            setLevel(newFluid, currentLevel + transfer.levels(), transaction);
        }
        return transfer.amount();
    }

    @Override
    public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
        Objects.checkIndex(index, size());
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        BlockState state = this.world.getBlockState(this.pos);
        Fluid currentFluid = getFluid(state);
        if (currentFluid == Fluids.EMPTY || resource.getFluid() != currentFluid || !resource.isComponentsPatchEmpty()) {
            return 0;
        }
        int currentLevel = getLevel(state);
        FluidUnits.Transfer transfer = FluidUnits.transfer(amount, currentLevel);
        if (transfer.levels() > 0) {
            setLevel(currentFluid, currentLevel - transfer.levels(), transaction);
        }
        return transfer.amount();
    }

    @Override
    protected BlockState createSnapshot() {
        return this.world.getBlockState(this.pos);
    }

    @Override
    protected void revertToSnapshot(BlockState snapshot) {
        this.world.setBlock(this.pos, snapshot, 0);
    }

    @Override
    protected void onRootCommit(BlockState originalState) {
        BlockState state = this.world.getBlockState(this.pos);
        if (originalState != state) {
            this.world.setBlock(this.pos, originalState, 0);
            this.world.setBlockAndUpdate(this.pos, state);
        }
    }

    @Override
    public String toString() {
        return "LeveledFluidContainerHandler[" + this.world + ", " + this.pos + "]";
    }
}
