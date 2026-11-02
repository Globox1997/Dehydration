package net.dehydration.item.storage;

import net.dehydration.block.entity.BambooPumpEntity;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.world.item.ItemStack;

public class BambooPumpItemStorage extends SingleStackStorage {

    private final BambooPumpEntity pumpEntity;

    public BambooPumpItemStorage(BambooPumpEntity pumpEntity) {
        this.pumpEntity = pumpEntity;
    }

    @Override
    protected int getCapacity(ItemVariant itemVariant) {
        return 1;
    }

    @Override
    protected ItemStack getStack() {
        return pumpEntity.getItem(0);
    }

    @Override
    protected void setStack(ItemStack stack) {
        pumpEntity.setItem(0, stack);
    }

    @Override
    protected boolean canInsert(ItemVariant variant) {
        //only items with a fluid storage are allowed
        Storage<FluidVariant> storage = ContainerItemContext.withConstant(variant, 1).find(FluidStorage.ITEM);
        return storage != null;
    }

}
