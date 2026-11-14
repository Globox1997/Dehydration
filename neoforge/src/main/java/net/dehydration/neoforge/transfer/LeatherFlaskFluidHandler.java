package net.dehydration.neoforge.transfer;

import java.util.Objects;

import net.dehydration.init.FluidInit;
import net.dehydration.init.ItemInit;
import net.dehydration.item.LeatherFlask;
import net.dehydration.item.component.FlaskComponent;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public final class LeatherFlaskFluidHandler implements ResourceHandler<FluidResource> {

    private final ItemAccess itemAccess;

    public LeatherFlaskFluidHandler(ItemAccess itemAccess) {
        this.itemAccess = itemAccess;
    }

    private int getFillLevel() {
        FlaskComponent data = this.itemAccess.getResource().getComponents().get(ItemInit.FLASK_DATA);
        return data == null ? 0 : Math.max(data.fillLevel(), 0);
    }

    private int getQualityLevel() {
        FlaskComponent data = this.itemAccess.getResource().getComponents().get(ItemInit.FLASK_DATA);
        return data == null || data.fillLevel() <= 0 ? 0 : data.qualityLevel();
    }

    private int getMaxLevel() {
        return this.itemAccess.getResource().getItem() instanceof LeatherFlask flask ? 2 + flask.getExtraFillLevel() : 0;
    }

    private int exchange(FlaskComponent data, int amount, TransactionContext transaction) {
        return this.itemAccess.exchange(this.itemAccess.getResource().with(ItemInit.FLASK_DATA, data), 1, transaction) == 1 ? amount : 0;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public FluidResource getResource(int index) {
        Objects.checkIndex(index, size());
        if (getFillLevel() <= 0) {
            return FluidResource.EMPTY;
        }
        return FluidResource.of(getQualityLevel() > 0 ? Fluids.WATER : FluidInit.PURIFIED_WATER);
    }

    @Override
    public long getAmountAsLong(int index) {
        Objects.checkIndex(index, size());
        return (long) getFillLevel() * FluidUnits.BOTTLE;
    }

    @Override
    public long getCapacityAsLong(int index, FluidResource resource) {
        Objects.checkIndex(index, size());
        return resource.isEmpty() || FluidUnits.isWater(resource) ? (long) getMaxLevel() * FluidUnits.BOTTLE : 0;
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
        int fillLevel = getFillLevel();
        int qualityLevel = getQualityLevel();
        FluidUnits.Transfer transfer = FluidUnits.transfer(amount, getMaxLevel() - fillLevel);
        if (transfer.levels() <= 0) {
            return 0;
        }
        if (resource.getFluid() != FluidInit.PURIFIED_WATER) {
            int dirtyAddition = (transfer.levels() > fillLevel) ? 2 : 1;
            qualityLevel = Math.min(qualityLevel + dirtyAddition, 2);
        }
        return exchange(new FlaskComponent(fillLevel + transfer.levels(), qualityLevel), transfer.amount(), transaction);
    }

    @Override
    public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
        Objects.checkIndex(index, size());
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        if (!resource.equals(getResource(index))) {
            return 0;
        }
        int fillLevel = getFillLevel();
        FluidUnits.Transfer transfer = FluidUnits.transfer(amount, fillLevel);
        if (transfer.levels() <= 0) {
            return 0;
        }
        int newFillLevel = fillLevel - transfer.levels();
        FlaskComponent data = newFillLevel <= 0 ? new FlaskComponent(0, 0) : new FlaskComponent(newFillLevel, getQualityLevel());
        return exchange(data, transfer.amount(), transaction);
    }

    @Override
    public String toString() {
        return "LeatherFlaskFluidHandler[" + this.itemAccess + "]";
    }
}
