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
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.registry.entry.RegistryEntry;
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
        Optional<RegistryEntry<Potion>> potion = variant.toStack().getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT).potion();
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
        newStack.set(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);
        return ItemVariant.of(Items.GLASS_BOTTLE, newStack.getComponentChanges());
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
