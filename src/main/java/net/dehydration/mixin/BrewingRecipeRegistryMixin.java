package net.dehydration.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.dehydration.init.ItemInit;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;

@Mixin(BrewingRecipeRegistry.class)
public class BrewingRecipeRegistryMixin {

    @Inject(method = "registerDefaults", at = @At("TAIL"))
    private static void registerDefaultsMixin(CallbackInfo info) {
        registerPotionRecipe(Potions.WATER, Items.CHARCOAL, ItemInit.PURIFIED_WATER);
        registerPotionRecipe(Potions.WATER, Items.KELP, ItemInit.PURIFIED_WATER);
        registerPotionRecipe(ItemInit.PURIFIED_WATER, Items.GHAST_TEAR, ItemInit.HYDRATION);
    }

    @Shadow
    private static void registerPotionRecipe(Potion input, Item item, Potion output) {
    }
}
