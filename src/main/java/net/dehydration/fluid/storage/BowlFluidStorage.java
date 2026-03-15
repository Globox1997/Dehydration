package net.dehydration.fluid.storage;

import net.dehydration.init.FluidInit;
import net.dehydration.init.ItemInit;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class BowlFluidStorage extends SingleVariantStorage<FluidVariant> {

    private final static long BOWL_CAPACITY = FluidConstants.BOTTLE;

    private final ItemStack stack;
    private final ContainerItemContext context;

    public BowlFluidStorage(ItemStack stack, ContainerItemContext context) {
        this.stack = stack;
        this.context = context;
    }

    @Override
    protected FluidVariant getBlankVariant() {
        return FluidVariant.blank();
    }

    @Override
    public boolean isResourceBlank() {
        return stack.isOf(Items.BOWL);
    }

    @Override
    public FluidVariant getResource() {
        if (stack.isOf(ItemInit.WATER_BOWL)) {
            return FluidVariant.of(Fluids.WATER);
        } else if (stack.isOf(ItemInit.PURIFIED_WATER_BOWL)) {
            return FluidVariant.of(FluidInit.PURIFIED_WATER);
        }
        return getBlankVariant();
    }

    @Override
    public long getAmount() {
        return (stack.isOf(ItemInit.WATER_BOWL) || stack.isOf(ItemInit.PURIFIED_WATER_BOWL)) ? BOWL_CAPACITY : 0;
    }

    @Override
    protected long getCapacity(FluidVariant variant) {
        return BOWL_CAPACITY;
    }

    @Override
    protected boolean canExtract(FluidVariant variant) {
        if (variant.isOf(Fluids.WATER)) {
            return stack.isOf(ItemInit.WATER_BOWL) || stack.isOf(ItemInit.PURIFIED_WATER_BOWL);
        } else if (variant.isOf(FluidInit.PURIFIED_WATER)) {
            return stack.isOf(ItemInit.PURIFIED_WATER_BOWL);
        }
        return false;
    }

    @Override
    protected boolean canInsert(FluidVariant variant) {
        return stack.isOf(Items.BOWL) && (variant.getFluid() == Fluids.WATER || variant.getFluid() == FluidInit.PURIFIED_WATER);
    }

    @Override
    public long insert(FluidVariant insertedVariant, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(insertedVariant, maxAmount);
        if (!canInsert(insertedVariant)) {
            return 0;
        }
        if (maxAmount < BOWL_CAPACITY) {
            return 0;
        }

        ItemStack newStack = new ItemStack(insertedVariant.isOf(FluidInit.PURIFIED_WATER) ? ItemInit.PURIFIED_WATER_BOWL : ItemInit.WATER_BOWL);
        if (context.exchange(ItemVariant.of(newStack), 1, transaction) == 1) {
            return BOWL_CAPACITY;
        }
        return 0;
    }

    @Override
    public long extract(FluidVariant extractedVariant, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(extractedVariant, maxAmount);
        if (!canExtract(extractedVariant)) {
            return 0;
        }
        if (maxAmount < BOWL_CAPACITY) {
            return 0;
        }

        ItemStack newStack = new ItemStack(Items.BOWL);
        if (context.exchange(ItemVariant.of(newStack), 1, transaction) == 1) {
            return BOWL_CAPACITY;
        }
        return 0;
    }
}
