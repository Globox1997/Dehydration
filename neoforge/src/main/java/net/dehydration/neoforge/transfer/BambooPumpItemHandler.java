package net.dehydration.neoforge.transfer;

import net.dehydration.block.entity.BambooPumpEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStackResourceHandler;

public final class BambooPumpItemHandler extends ItemStackResourceHandler {

    private final BambooPumpEntity pumpEntity;

    public BambooPumpItemHandler(BambooPumpEntity pumpEntity) {
        this.pumpEntity = pumpEntity;
    }

    @Override
    protected ItemStack getStack() {
        return this.pumpEntity.getItem(0);
    }

    @Override
    protected void setStack(ItemStack stack) {
        this.pumpEntity.setItem(0, stack);
    }

    @Override
    protected boolean isValid(ItemResource resource) {
        return ItemAccess.forStack(resource.toStack(1)).getCapability(Capabilities.Fluid.ITEM) != null;
    }

    @Override
    protected int getCapacity(ItemResource resource) {
        return 1;
    }
}
