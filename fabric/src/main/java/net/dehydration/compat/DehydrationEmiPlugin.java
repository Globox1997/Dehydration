package net.dehydration.compat;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiWorldInteractionRecipe;
import dev.emi.emi.api.stack.EmiStack;
import net.dehydration.DehydrationMain;
import net.dehydration.init.FluidInit;
import net.dehydration.init.ItemInit;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.material.Fluids;

public class DehydrationEmiPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        ItemStack purifiedWaterBottle = ItemVariant.of(Items.POTION).toStack();
        purifiedWaterBottle.set(DataComponents.POTION_CONTENTS, new PotionContents(ItemInit.PURIFIED_WATER));

        registry.addRecipe(EmiWorldInteractionRecipe.builder().id(DehydrationMain.identifierOf("/emi/recipe/water_bowl")).leftInput(EmiStack.of(Items.BOWL)).rightInput(EmiStack.of(Fluids.WATER), false).output(EmiStack.of(ItemInit.WATER_BOWL)).build());
        registry.addRecipe(EmiWorldInteractionRecipe.builder().id(DehydrationMain.identifierOf("/emi/recipe/purified_water_bowl")).leftInput(EmiStack.of(Items.BOWL)).rightInput(EmiStack.of(FluidInit.PURIFIED_WATER), false).output(EmiStack.of(ItemInit.PURIFIED_WATER_BOWL)).build());
        registry.addRecipe(EmiWorldInteractionRecipe.builder().id(DehydrationMain.identifierOf("/emi/recipe/purified_water_bottle")).leftInput(EmiStack.of(Items.GLASS_BOTTLE)).rightInput(EmiStack.of(FluidInit.PURIFIED_WATER), false).output(EmiStack.of(purifiedWaterBottle)).build());
    }
}
