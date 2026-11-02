package net.dehydration.fluid.storage;

import net.dehydration.init.FluidInit;
import net.dehydration.init.ItemInit;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ExtractionOnlyStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PurifiedWaterPotionStorage implements ExtractionOnlyStorage<FluidVariant>, SingleSlotStorage<FluidVariant> {
    private static final FluidVariant CONTAINED_FLUID = FluidVariant.of(FluidInit.PURIFIED_WATER);
    private static final long CONTAINED_AMOUNT = FluidConstants.BOTTLE;

    @Nullable
    public static PurifiedWaterPotionStorage find(ContainerItemContext context) {
        return isPurifiedWaterPotion(context) ? new PurifiedWaterPotionStorage(context) : null;
    }

    private static boolean isPurifiedWaterPotion(ContainerItemContext context) {
        ItemVariant variant = context.getItemVariant();
        if (!variant.isOf(Items.POTION)) {
            return false;
        }
        Optional<Holder<Potion>> potion = variant.toStack().getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).potion();
        return potion.isPresent() && potion.get() == ItemInit.PURIFIED_WATER;
    }

    private final ContainerItemContext context;

    private PurifiedWaterPotionStorage(ContainerItemContext context) {
        this.context = context;
    }

    private boolean isPurifiedWaterPotion() {
        return isPurifiedWaterPotion(context);
    }

    private ItemVariant mapToGlassBottle() {
        ItemStack newStack = context.getItemVariant().toStack();
        newStack.set(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        return ItemVariant.of(Items.GLASS_BOTTLE, newStack.getComponentsPatch());
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);

        if (!isPurifiedWaterPotion()) return 0;

        if (resource.equals(CONTAINED_FLUID) && maxAmount >= CONTAINED_AMOUNT) {
            if (context.exchange(mapToGlassBottle(), 1, transaction) == 1) {
                return CONTAINED_AMOUNT;
            }
        }

        return 0;
    }

    @Override
    public boolean isResourceBlank() {
        return getResource().isBlank();
    }

    @Override
    public FluidVariant getResource() {
        if (isPurifiedWaterPotion()) {
            return CONTAINED_FLUID;
        } else {
            return FluidVariant.blank();
        }
    }

    @Override
    public long getAmount() {
        if (isPurifiedWaterPotion()) {
            return CONTAINED_AMOUNT;
        } else {
            return 0;
        }
    }

    @Override
    public long getCapacity() {
        return getAmount();
    }

    @Override
    public String toString() {
        return "PurifiedWaterPotionStorage[" + context + "]";
    }
}
