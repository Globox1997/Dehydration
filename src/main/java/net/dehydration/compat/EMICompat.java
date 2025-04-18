package net.dehydration.compat;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiWorldInteractionRecipe;
import dev.emi.emi.api.stack.EmiStack;
import net.dehydration.init.FluidInit;
import net.dehydration.init.ItemInit;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class EMICompat implements EmiPlugin {
        
    @Override
    public void register(EmiRegistry registry) {
        ItemStack purified_water_bottle = ItemVariant.of(Items.POTION).toStack();
        purified_water_bottle.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(ItemInit.PURIFIED_WATER));

        registry.addRecipe(EmiWorldInteractionRecipe.builder().id(Identifier.of("dehydration", "emi/recipe/water_bowl")).leftInput(EmiStack.of(Items.BOWL)).rightInput(EmiStack.of(Fluids.WATER), false).output(EmiStack.of(ItemInit.WATER_BOWL)).build());
        registry.addRecipe(EmiWorldInteractionRecipe.builder().id(Identifier.of("dehydration", "emi/recipe/purified_water_bowl")).leftInput(EmiStack.of(Items.BOWL)).rightInput(EmiStack.of(FluidInit.PURIFIED_WATER), false).output(EmiStack.of(ItemInit.PURIFIED_WATER_BOWL)).build());
        registry.addRecipe(EmiWorldInteractionRecipe.builder().id(Identifier.of("dehydration", "emi/recipe/purified_water_bottle")).leftInput(EmiStack.of(Items.GLASS_BOTTLE)).rightInput(EmiStack.of(FluidInit.PURIFIED_WATER), false).output(EmiStack.of(purified_water_bottle)).build());
    }
}
