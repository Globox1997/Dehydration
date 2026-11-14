package net.dehydration.neoforge.transfer;

import java.util.Objects;

import net.dehydration.block.CampfireCauldronBlock;
import net.dehydration.block.entity.CampfireCauldronEntity;
import net.dehydration.init.FluidInit;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public final class CampfireCauldronFluidHandler extends SnapshotJournal<CampfireCauldronFluidHandler.Content> implements ResourceHandler<FluidResource> {

    private static final int MAX_LEVEL = 4;

    private final Level world;
    private final BlockPos pos;
    private final BlockState state;
    private final CampfireCauldronEntity campfireCauldronEntity;
    private Content content;

    public CampfireCauldronFluidHandler(Level world, BlockPos pos, BlockState state, CampfireCauldronEntity campfireCauldronEntity) {
        this.world = world;
        this.pos = pos;
        this.state = state;
        this.campfireCauldronEntity = campfireCauldronEntity;
        int level = state.getValue(CampfireCauldronBlock.LEVEL);
        Fluid fluid = level > 0 ? (campfireCauldronEntity.isBoiled ? FluidInit.PURIFIED_WATER : Fluids.WATER) : Fluids.EMPTY;
        this.content = new Content(level, fluid, false);
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public FluidResource getResource(int index) {
        Objects.checkIndex(index, size());
        return FluidResource.of(this.content.fluid());
    }

    @Override
    public long getAmountAsLong(int index) {
        Objects.checkIndex(index, size());
        return (long) this.content.level() * FluidUnits.BOTTLE;
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
        if (!FluidUnits.isWater(resource) || this.content.level() >= MAX_LEVEL) {
            return 0;
        }
        FluidUnits.Transfer transfer = FluidUnits.transfer(amount, MAX_LEVEL - this.content.level());
        if (transfer.levels() <= 0) {
            return 0;
        }
        updateSnapshots(transaction);
        Fluid insertedFluid = resource.getFluid();
        boolean updateBoiling = this.content.updateBoiling() || this.content.fluid() != FluidInit.PURIFIED_WATER || insertedFluid != FluidInit.PURIFIED_WATER;
        Fluid newFluid = this.content.fluid();
        if (newFluid == Fluids.EMPTY) {
            newFluid = insertedFluid;
        } else if (newFluid != insertedFluid) {
            newFluid = Fluids.WATER;
        }
        this.content = new Content(this.content.level() + transfer.levels(), newFluid, updateBoiling);
        return transfer.amount();
    }

    @Override
    public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
        Objects.checkIndex(index, size());
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        if (this.content.level() <= 0 || resource.getFluid() != this.content.fluid() || !resource.isComponentsPatchEmpty()) {
            return 0;
        }
        FluidUnits.Transfer transfer = FluidUnits.transfer(amount, this.content.level());
        if (transfer.levels() <= 0) {
            return 0;
        }
        updateSnapshots(transaction);
        int newLevel = this.content.level() - transfer.levels();
        this.content = new Content(newLevel, newLevel <= 0 ? Fluids.EMPTY : this.content.fluid(), this.content.updateBoiling());
        return transfer.amount();
    }

    @Override
    protected Content createSnapshot() {
        return this.content;
    }

    @Override
    protected void revertToSnapshot(Content snapshot) {
        this.content = snapshot;
    }

    @Override
    protected void onRootCommit(Content originalState) {
        if (this.state.getBlock() instanceof CampfireCauldronBlock campfireCauldronBlock) {
            campfireCauldronBlock.setLevel(this.world, this.pos, this.state, this.content.level());
            if (this.content.updateBoiling()) {
                this.campfireCauldronEntity.onFillingCauldron();
            }
        }
    }

    public record Content(int level, Fluid fluid, boolean updateBoiling) {
    }
}
