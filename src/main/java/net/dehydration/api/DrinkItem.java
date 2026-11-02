package net.dehydration.api;

import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.util.Prediction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;

/**
 * The {@link DrinkItem} represents any drinkable item
 */
public class DrinkItem extends Item {
    public DrinkItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);
        FoodProperties foodComponent = stack.get(DataComponents.FOOD);
        if (foodComponent != null && !stack.has(DataComponents.CONSUMABLE)) {
            if (user.canEat(foodComponent.canAlwaysEat())) {
                user.startUsingItem(hand);
                return InteractionResult.CONSUME;
            }
            return InteractionResult.FAIL;
        }
        return super.use(world, user, hand);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        if (stack.has(DataComponents.FOOD) && !stack.has(DataComponents.CONSUMABLE)) {
            return 32;
        }
        return super.getUseDuration(stack, user);
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.DRINK;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        Player playerEntity = user instanceof Player ? (Player) user : null;

        // Trigger consumption of an item if it's the server
        if (playerEntity instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer) playerEntity, stack);
        }

        boolean consumed = false;
        if (playerEntity != null) {
            // Increment the use statistic
            playerEntity.awardStat(Stats.ITEM_USED.get(this));

            // Drinks are not available in creative mode
            if (!playerEntity.getAbilities().instabuild) {
                // The drink also has to have a FoodComponent
                FoodProperties foodComponent = stack.get(DataComponents.FOOD);
                if (foodComponent != null) {
                    DrinkEvent.EVENT.invoker().onDrink(stack, playerEntity);
                    Consumable consumable = stack.getOrDefault(DataComponents.CONSUMABLE, Consumables.DEFAULT_DRINK);
                    foodComponent.onConsume(world, user, stack, consumable);
                    if (!world.isClientSide()) {
                        consumable.onConsumeEffects().forEach(effect -> effect.apply(world, stack, user));
                    }
                    user.gameEvent(GameEvent.DRINK);
                    stack.consume(1, user);
                    consumed = true;
                }
            }
        }

        // Create a glass bottle after the item is consumed
        if (consumed) {
            if (stack.isEmpty()) {
                return new ItemStack(Items.GLASS_BOTTLE);
            }

            // offerOrDrop is generally safer to use than insertStack
            playerEntity.getInventory().placeItemBackInInventory(new ItemStack(Items.GLASS_BOTTLE), Prediction.PREDICTED);
        }

        return stack;
    }
}
