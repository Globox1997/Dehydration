package net.dehydration.fluid.storage;

import net.dehydration.init.FluidInit;
import net.dehydration.init.ItemInit;
import net.dehydration.item.LeatherFlask;
import net.dehydration.item.component.FlaskComponent;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

public class LeatherFlaskFluidStorage extends SingleVariantStorage<FluidVariant> {

    private final static FluidVariant DIRTY_WATER = FluidVariant.of(Fluids.WATER);
    private final static FluidVariant PURIFIED_WATER = FluidVariant.of(FluidInit.PURIFIED_WATER);

    private final ContainerItemContext context;
    private final LeatherFlask flaskItem;
    private final Map<ResourceAmount<FluidVariant>, Integer> qualitySnapshots = new IdentityHashMap<>();

    private int fillLevel = 0;
    private int qualityLevel = 0;

    public LeatherFlaskFluidStorage(ContainerItemContext context) {
        this.context = context;
        ItemVariant itemVariant = context.getItemVariant();
        this.flaskItem = (LeatherFlask) itemVariant.getItem();
        Optional<? extends FlaskComponent> optional = itemVariant.getComponents().get(ItemInit.FLASK_DATA);
        if (optional != null) {
            optional.ifPresent(data -> {
                if (data.fillLevel() > 0) {
                    this.fillLevel = data.fillLevel();
                    this.qualityLevel = data.qualityLevel();
                    this.variant = (this.qualityLevel > 0) ? DIRTY_WATER : PURIFIED_WATER;
                    this.amount = this.fillLevel * FluidConstants.BOTTLE;
                }
            });
        }
    }

    @Override
    protected FluidVariant getBlankVariant() {
        return FluidVariant.blank();
    }

    private int getMaxLevel() {
        return 2 + this.flaskItem.getExtraFillLevel();
    }

    @Override
    protected long getCapacity(FluidVariant variant) {
        return FluidConstants.BOTTLE * (long) getMaxLevel();
    }

    @Override
    protected boolean canExtract(FluidVariant variant) {
        return this.variant.getFluid() == variant.getFluid() && this.fillLevel > 0;
    }

    @Override
    protected boolean canInsert(FluidVariant variant) {
        return (variant.getFluid() == Fluids.WATER || variant.getFluid() == FluidInit.PURIFIED_WATER) && this.fillLevel < getMaxLevel();
    }

    @Override
    public long insert(FluidVariant insertedVariant, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(insertedVariant, maxAmount);
        if (!canInsert(insertedVariant)) {
            return 0;
        }
        updateSnapshots(transaction);
        int levelIncrease = Math.min((int) (maxAmount / FluidConstants.BOTTLE), getMaxLevel() - this.fillLevel);
        if (levelIncrease <= 0) {
            return 0;
        }
        if (insertedVariant.getFluid() != FluidInit.PURIFIED_WATER) {
            int dirtyAddition = (levelIncrease > this.fillLevel) ? 2 : 1;
            this.qualityLevel = Math.min(this.qualityLevel + dirtyAddition, 2);
        }
        this.fillLevel += levelIncrease;
        long insertedAmount = levelIncrease * FluidConstants.BOTTLE;
        this.amount += insertedAmount;
        this.variant = (this.qualityLevel) > 0 ? DIRTY_WATER : PURIFIED_WATER;
        ItemStack stack = context.getItemVariant().toStack();
        stack.set(ItemInit.FLASK_DATA, new FlaskComponent(this.fillLevel, this.qualityLevel));
        if (context.exchange(ItemVariant.of(stack), 1, transaction) == 1) {
            return insertedAmount;
        }
        return 0;
    }

    @Override
    public long extract(FluidVariant extractedVariant, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(extractedVariant, maxAmount);
        if (!canExtract(extractedVariant)) {
            return 0;
        }
        updateSnapshots(transaction);
        int levelDecrease = Math.min((int) (maxAmount / FluidConstants.BOTTLE), this.fillLevel);
        if (levelDecrease <= 0) {
            return 0;
        }
        this.fillLevel -= levelDecrease;
        long extractedAmount = levelDecrease * FluidConstants.BOTTLE;
        this.amount -= extractedAmount;
        if (this.amount <= 0 || this.fillLevel <= 0) {
            this.fillLevel = 0;
            this.qualityLevel = 0;
            this.amount = 0;
            this.variant = this.getBlankVariant();
        }
        ItemStack stack = context.getItemVariant().toStack();
        stack.set(ItemInit.FLASK_DATA, new FlaskComponent(this.fillLevel, this.qualityLevel));
        if (context.exchange(ItemVariant.of(stack), 1, transaction) == 1) {
            return extractedAmount;
        }
        return 0;
    }

    @Override
    protected void releaseSnapshot(ResourceAmount<FluidVariant> snapshot) {
        this.qualitySnapshots.remove(snapshot);
        super.releaseSnapshot(snapshot);
    }

    @Override
    protected ResourceAmount<FluidVariant> createSnapshot() {
        ResourceAmount<FluidVariant> snapshot = super.createSnapshot();
        this.qualitySnapshots.put(snapshot, this.qualityLevel);
        return snapshot;
    }

    @Override
    protected void readSnapshot(ResourceAmount<FluidVariant> snapshot) {
        super.readSnapshot(snapshot);
        this.fillLevel = Math.min((int) (this.amount / FluidConstants.BOTTLE), getMaxLevel());
        this.qualityLevel = this.qualitySnapshots.get(snapshot);
    }

}
