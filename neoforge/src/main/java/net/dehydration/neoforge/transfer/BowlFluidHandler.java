package net.dehydration.neoforge.transfer;

import java.util.Objects;

import net.dehydration.init.FluidInit;
import net.dehydration.init.ItemInit;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public final class BowlFluidHandler implements ResourceHandler<FluidResource> {

    private static final int BOWL_CAPACITY = FluidUnits.BOTTLE * 2;

    private final ItemAccess itemAccess;

    public BowlFluidHandler(ItemAccess itemAccess) {
        this.itemAccess = itemAccess;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public FluidResource getResource(int index) {
        Objects.checkIndex(index, size());
        ItemResource resource = this.itemAccess.getResource();
        if (resource.is(ItemInit.WATER_BOWL)) {
            return FluidResource.of(Fluids.WATER);
        } else if (resource.is(ItemInit.PURIFIED_WATER_BOWL)) {
            return FluidResource.of(FluidInit.PURIFIED_WATER);
        }
        return FluidResource.EMPTY;
    }

    @Override
    public long getAmountAsLong(int index) {
        Objects.checkIndex(index, size());
        return getResource(index).isEmpty() ? 0 : (long) this.itemAccess.getAmount() * BOWL_CAPACITY;
    }

    @Override
    public long getCapacityAsLong(int index, FluidResource resource) {
        Objects.checkIndex(index, size());
        return resource.isEmpty() || FluidUnits.isWater(resource) ? (long) this.itemAccess.getAmount() * BOWL_CAPACITY : 0;
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
        if (!this.itemAccess.getResource().is(Items.BOWL) || !FluidUnits.isWater(resource) || amount < BOWL_CAPACITY) {
            return 0;
        }
        ItemResource filledBowl = ItemResource.of(resource.getFluid() == FluidInit.PURIFIED_WATER ? ItemInit.PURIFIED_WATER_BOWL : ItemInit.WATER_BOWL);
        return this.itemAccess.exchange(filledBowl, 1, transaction) == 1 ? BOWL_CAPACITY : 0;
    }

    @Override
    public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
        Objects.checkIndex(index, size());
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        if (!resource.isComponentsPatchEmpty() || amount < BOWL_CAPACITY) {
            return 0;
        }
        ItemResource current = this.itemAccess.getResource();
        boolean canExtract;
        if (resource.getFluid() == Fluids.WATER) {
            canExtract = current.is(ItemInit.WATER_BOWL) || current.is(ItemInit.PURIFIED_WATER_BOWL);
        } else if (resource.getFluid() == FluidInit.PURIFIED_WATER) {
            canExtract = current.is(ItemInit.PURIFIED_WATER_BOWL);
        } else {
            canExtract = false;
        }
        if (!canExtract) {
            return 0;
        }
        return this.itemAccess.exchange(ItemResource.of(Items.BOWL), 1, transaction) == 1 ? BOWL_CAPACITY : 0;
    }

    @Override
    public String toString() {
        return "BowlFluidHandler[" + this.itemAccess + "]";
    }
}
