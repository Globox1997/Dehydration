package net.dehydration.util;

import net.dehydration.DehydrationMain;
import net.dehydration.access.ThirstManagerAccess;
import net.dehydration.init.ConfigInit;
import net.dehydration.init.EffectInit;
import net.dehydration.init.TagInit;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;

public class ThirstHelper {

    public static void hydratePlayer(ServerPlayerEntity serverPlayerEntity, ItemStack stack) {
        int thirstQuench = 0;
        if (stack.isIn(TagInit.HYDRATING_STEW)) {
            thirstQuench = ConfigInit.CONFIG.stew_thirst_quench;
        }
        if (stack.isIn(TagInit.HYDRATING_FOOD)) {
            thirstQuench = ConfigInit.CONFIG.food_thirst_quench;
        }
        if (stack.isIn(TagInit.HYDRATING_DRINKS)) {
            thirstQuench = ConfigInit.CONFIG.drinks_thirst_quench;
        }
        if (stack.isIn(TagInit.STRONGER_HYDRATING_STEW)) {
            thirstQuench = ConfigInit.CONFIG.stronger_stew_thirst_quench;
        }
        if (stack.isIn(TagInit.STRONGER_HYDRATING_FOOD)) {
            thirstQuench = ConfigInit.CONFIG.stronger_food_thirst_quench;
        }
        if (stack.isIn(TagInit.STRONGER_HYDRATING_DRINKS)) {
            thirstQuench = ConfigInit.CONFIG.stronger_drinks_thirst_quench;
        }

        for (int i = 0; i < DehydrationMain.HYDRATION_TEMPLATES.size(); i++) {
            if (DehydrationMain.HYDRATION_TEMPLATES.get(i).containsItem(stack.getItem())) {
                thirstQuench = DehydrationMain.HYDRATION_TEMPLATES.get(i).getHydration();
                break;
            }
        }
        if (stack.getItem() instanceof PotionItem) {
            PotionContentsComponent potionContentsComponent = stack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);
            if (ThirstHelper.isBadPotion(potionContentsComponent.potion().orElse(Potions.WATER)) && serverPlayerEntity.getRandom().nextFloat() >= ConfigInit.CONFIG.potion_bad_thirst_chance) {
                serverPlayerEntity.addStatusEffect(new StatusEffectInstance(EffectInit.THIRST, ConfigInit.CONFIG.potion_bad_thirst_duration, 0, false, false, true));
            }
            if (thirstQuench == 0) {
                thirstQuench = ConfigInit.CONFIG.potion_thirst_quench;
            }
        } else if (stack.getItem() instanceof MilkBucketItem) {
            if (serverPlayerEntity.getRandom().nextFloat() >= ConfigInit.CONFIG.milk_thirst_chance) {
                serverPlayerEntity.addStatusEffect(new StatusEffectInstance(EffectInit.THIRST, ConfigInit.CONFIG.potion_bad_thirst_duration / 2, 0, false, false, true));
            }
            if (thirstQuench == 0) {
                thirstQuench = ConfigInit.CONFIG.milk_thirst_quench;
            }
        } else if (thirstQuench == 0) {
            if (stack.getItem() instanceof HoneyBottleItem) {
                thirstQuench = ConfigInit.CONFIG.honey_quench;
            }
        }
        if (thirstQuench > 0) {
            ((ThirstManagerAccess) serverPlayerEntity).getThirstManager().add(thirstQuench);
        }
    }

    public static boolean isBadPotion(RegistryEntry<Potion> potion) {
        return potion == Potions.WATER || potion == Potions.AWKWARD || potion == Potions.THICK || potion == Potions.HARMING || potion == Potions.LONG_POISON || potion == Potions.LONG_SLOWNESS
                || potion == Potions.LONG_WEAKNESS || potion == Potions.MUNDANE || potion == Potions.POISON || potion == Potions.SLOWNESS || potion == Potions.STRONG_HARMING
                || potion == Potions.STRONG_POISON || potion == Potions.STRONG_SLOWNESS || potion == Potions.WEAKNESS;
    }

}
