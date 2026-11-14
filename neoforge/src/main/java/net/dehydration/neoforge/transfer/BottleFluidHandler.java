package net.dehydration.neoforge.transfer;

import java.util.Objects;
import java.util.Optional;

import net.dehydration.init.FluidInit;
import net.dehydration.init.ItemInit;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public final class BottleFluidHandler implements ResourceHandler<FluidResource> {

    private final ItemAccess itemAccess;

    public BottleFluidHandler(ItemAccess itemAccess) {
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
        if (!resource.is(Items.POTION)) {
            return FluidResource.EMPTY;
        }
        PotionContents contents = resource.getComponents().get(DataComponents.POTION_CONTENTS);
        Optional<Holder<Potion>> potion = contents == null ? Optional.empty() : contents.potion();
        if (potion.isPresent() && potion.get().is(Potions.WATER)) {
            return FluidResource.of(Fluids.WATER);
        } else if (potion.isPresent() && potion.get() == ItemInit.PURIFIED_WATER) {
            return FluidResource.of(FluidInit.PURIFIED_WATER);
        }
        return FluidResource.EMPTY;
    }

    @Override
    public long getAmountAsLong(int index) {
        Objects.checkIndex(index, size());
        return getResource(index).isEmpty() ? 0 : (long) this.itemAccess.getAmount() * FluidUnits.BOTTLE;
    }

    @Override
    public long getCapacityAsLong(int index, FluidResource resource) {
        Objects.checkIndex(index, size());
        return resource.isEmpty() || FluidUnits.isWater(resource) ? (long) this.itemAccess.getAmount() * FluidUnits.BOTTLE : 0;
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
        if (!this.itemAccess.getResource().is(Items.GLASS_BOTTLE) || !FluidUnits.isWater(resource) || amount < FluidUnits.BOTTLE) {
            return 0;
        }
        Holder<Potion> potion = resource.getFluid() == FluidInit.PURIFIED_WATER ? ItemInit.PURIFIED_WATER : Potions.WATER;
        ItemResource filledBottle = ItemResource.of(PotionContents.createItemStack(Items.POTION, potion));
        return this.itemAccess.exchange(filledBottle, 1, transaction) == 1 ? FluidUnits.BOTTLE : 0;
    }

    @Override
    public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
        Objects.checkIndex(index, size());
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        if (amount < FluidUnits.BOTTLE || !resource.equals(getResource(index))) {
            return 0;
        }
        return this.itemAccess.exchange(ItemResource.of(Items.GLASS_BOTTLE), 1, transaction) == 1 ? FluidUnits.BOTTLE : 0;
    }

    @Override
    public String toString() {
        return "BottleFluidHandler[" + this.itemAccess + "]";
    }
}
