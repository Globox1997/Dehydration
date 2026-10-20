package net.dehydration.fluid.storage;

import net.dehydration.block.CampfireCauldronBlock;
import net.dehydration.block.entity.CampfireCauldronEntity;
import net.dehydration.init.FluidInit;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CampfireCauldronFluidStorage extends SingleVariantStorage<FluidVariant> {

    private static final long CAPACITY = FluidConstants.BOTTLE * 4L;

    private final World world;
    private final BlockPos pos;
    private final BlockState state;
    private final CampfireCauldronEntity campfireCauldronEntity;

    private int fluidLevel;
    private boolean updateBoiling = false;

    public CampfireCauldronFluidStorage(World world, BlockPos pos, BlockState state, CampfireCauldronEntity campfireCauldronEntity) {
        this.world = world;
        this.pos = pos;
        this.state = state;
        this.campfireCauldronEntity = campfireCauldronEntity;
        this.fluidLevel = state.get(CampfireCauldronBlock.LEVEL);
        if (this.fluidLevel > 0) {
            this.variant = campfireCauldronEntity.isBoiled ? FluidVariant.of(FluidInit.PURIFIED_WATER) : FluidVariant.of(Fluids.WATER);
            this.amount = this.fluidLevel * FluidConstants.BOTTLE;
        }
    }

    @Override
    protected FluidVariant getBlankVariant() {
        return FluidVariant.blank();
    }

    @Override
    protected long getCapacity(FluidVariant variant) {
        return CAPACITY;
    }

    @Override
    protected boolean canExtract(FluidVariant variant) {
        return this.variant.getFluid() == variant.getFluid() && this.fluidLevel > 0;
    }

    @Override
    protected boolean canInsert(FluidVariant variant) {
        return (variant.getFluid() == Fluids.WATER || variant.getFluid() == FluidInit.PURIFIED_WATER) && this.fluidLevel < 4;
    }

    @Override
    protected void readSnapshot(ResourceAmount<FluidVariant> snapshot) {
        super.readSnapshot(snapshot);
        this.fluidLevel = Math.min((int) (this.amount / FluidConstants.BOTTLE), 4);
        this.updateBoiling = !this.variant.isBlank() && this.variant.getFluid() != FluidInit.PURIFIED_WATER;
    }

    @Override
    public long insert(FluidVariant insertedVariant, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(insertedVariant, maxAmount);
        if (!canInsert(insertedVariant)) {
            return 0;
        }
        updateSnapshots(transaction);
        int levelIncrease = Math.min((int) (maxAmount / FluidConstants.BOTTLE), 4 - this.fluidLevel);
        if (levelIncrease <= 0) {
            return 0;
        }
        this.fluidLevel += levelIncrease;
        long insertedAmount = levelIncrease * FluidConstants.BOTTLE;
        this.amount += insertedAmount;
        if (this.variant.getFluid() != FluidInit.PURIFIED_WATER || insertedVariant.getFluid() != FluidInit.PURIFIED_WATER) {
            this.updateBoiling = true;
        }
        if (this.variant.isBlank()) {
            this.variant = insertedVariant;
        } else if (this.variant.getFluid() != insertedVariant.getFluid()) {
            this.variant = FluidVariant.of(Fluids.WATER);
        }
        return insertedAmount;
    }

    @Override
    public long extract(FluidVariant extractedVariant, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(extractedVariant, maxAmount);
        if (!canExtract(extractedVariant)) {
            return 0;
        }
        updateSnapshots(transaction);
        int levelDecrease = Math.min((int) (maxAmount / FluidConstants.BOTTLE), this.fluidLevel);
        if (levelDecrease <= 0) {
            return 0;
        }
        this.fluidLevel -= levelDecrease;
        long extractedAmount = levelDecrease * FluidConstants.BOTTLE;
        this.amount -= extractedAmount;
        if (this.amount <= 0 || this.fluidLevel <= 0) {
            this.fluidLevel = 0;
            this.amount = 0;
            this.variant = this.getBlankVariant();
        }
        return extractedAmount;
    }

    @Override
    protected void onFinalCommit() {
        if (this.state.getBlock() instanceof CampfireCauldronBlock campfireCauldronBlock) {
            campfireCauldronBlock.setLevel(this.world, this.pos, this.state, this.fluidLevel);
            if (this.updateBoiling) {
                this.campfireCauldronEntity.onFillingCauldron();
            }
        }
    }
}
