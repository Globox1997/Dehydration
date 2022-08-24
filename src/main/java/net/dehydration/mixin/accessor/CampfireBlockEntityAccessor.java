package net.dehydration.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

@Mixin(CampfireBlockEntity.class)
public abstract interface CampfireBlockEntityAccessor {

    @Accessor("itemsBeingCooked")
    public abstract DefaultedList<ItemStack> getItemsBeingCooked();
}
