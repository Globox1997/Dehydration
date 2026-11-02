package net.dehydration.util;

import net.dehydration.DehydrationMain;
import net.dehydration.access.ThirstManagerAccess;
import net.dehydration.api.DrinkItem;
import net.dehydration.api.HydrationTemplate;
import net.dehydration.init.ConfigInit;
import net.dehydration.init.EffectInit;
import net.dehydration.init.TagInit;
import net.dehydration.item.LeatherFlask;
import net.dehydration.item.WaterBowlItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoneyBottleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MilkBucketItem;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ThirstHelper {

    public static void hydratePlayer(ServerPlayer serverPlayerEntity, ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof DrinkItem || item instanceof LeatherFlask) {
            return;
        }
        if (item instanceof PotionItem) {
            PotionContents potionContentsComponent = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            if (ThirstHelper.isBadPotion(potionContentsComponent.potion().orElse(Potions.WATER)) && serverPlayerEntity.getRandom().nextFloat() < ConfigInit.CONFIG.potion_bad_thirst_chance) {
                serverPlayerEntity.addEffect(new MobEffectInstance(EffectInit.THIRST, ConfigInit.CONFIG.potion_bad_thirst_duration, 0, false, false, true));
            }
        } else if (item instanceof MilkBucketItem) {
            if (serverPlayerEntity.getRandom().nextFloat() < ConfigInit.CONFIG.milk_thirst_chance) {
                serverPlayerEntity.addEffect(new MobEffectInstance(EffectInit.THIRST, ConfigInit.CONFIG.potion_bad_thirst_duration / 2, 0, false, false, true));
            }
        }
        int thirstQuench = getThirstQuench(stack, false);
        if (thirstQuench > 0) {
            ((ThirstManagerAccess) serverPlayerEntity).getThirstManager().add(thirstQuench);
        }
    }

    public static int getThirstQuench(ItemStack stack, boolean client) {
        int thirstQuench = 0;
        if (stack.is(TagInit.HYDRATING_STEW)) {
            thirstQuench = ConfigInit.CONFIG.stew_thirst_quench;
        }
        if (stack.is(TagInit.HYDRATING_FOOD)) {
            thirstQuench = ConfigInit.CONFIG.food_thirst_quench;
        }
        if (stack.is(TagInit.HYDRATING_DRINKS)) {
            thirstQuench = ConfigInit.CONFIG.drinks_thirst_quench;
        }
        if (stack.is(TagInit.STRONGER_HYDRATING_STEW)) {
            thirstQuench = ConfigInit.CONFIG.stronger_stew_thirst_quench;
        }
        if (stack.is(TagInit.STRONGER_HYDRATING_FOOD)) {
            thirstQuench = ConfigInit.CONFIG.stronger_food_thirst_quench;
        }
        if (stack.is(TagInit.STRONGER_HYDRATING_DRINKS)) {
            thirstQuench = ConfigInit.CONFIG.stronger_drinks_thirst_quench;
        }
        int templateHydration = getTemplateHydration(stack.getItem(), client);
        if (templateHydration > 0) {
            thirstQuench = templateHydration;
        }
        if (thirstQuench == 0) {
            Item item = stack.getItem();
            if (item instanceof PotionItem) {
                thirstQuench = ConfigInit.CONFIG.potion_thirst_quench;
            } else if (item instanceof MilkBucketItem) {
                thirstQuench = ConfigInit.CONFIG.milk_thirst_quench;
            } else if (item instanceof HoneyBottleItem) {
                thirstQuench = ConfigInit.CONFIG.honey_quench;
            } else if (item instanceof WaterBowlItem) {
                thirstQuench = ConfigInit.CONFIG.water_bowl_quench;
            }
        }
        return thirstQuench;
    }

    public static int getTemplateHydration(Item item, boolean client) {
        List<HydrationTemplate> templates = client ? DehydrationMain.CLIENT_HYDRATION_TEMPLATES : DehydrationMain.HYDRATION_TEMPLATES;
        for (HydrationTemplate template : templates) {
            if (template.containsItem(item)) {
                return template.getHydration();
            }
        }
        return 0;
    }

    public static void drainWaterSource(@Nullable Player player, Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof BucketPickup fluidDrainable) {
            fluidDrainable.pickupBlock(player, world, pos, state);
        }
    }

    public static boolean isBadPotion(Holder<Potion> potion) {
        return potion == Potions.WATER || potion == Potions.AWKWARD || potion == Potions.THICK || potion == Potions.HARMING || potion == Potions.LONG_POISON || potion == Potions.LONG_SLOWNESS
                || potion == Potions.LONG_WEAKNESS || potion == Potions.MUNDANE || potion == Potions.POISON || potion == Potions.SLOWNESS || potion == Potions.STRONG_HARMING
                || potion == Potions.STRONG_POISON || potion == Potions.STRONG_SLOWNESS || potion == Potions.WEAKNESS;
    }

}
