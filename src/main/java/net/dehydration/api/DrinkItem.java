package net.dehydration.api;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/**
 * The {@link DrinkItem} represents any drinkable item
 */
public class DrinkItem extends Item {
    public DrinkItem(Properties settings) {
        super(settings);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
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
                    user.eat(world, stack, foodComponent);
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
            playerEntity.getInventory().placeItemBackInInventory(new ItemStack(Items.GLASS_BOTTLE));
        }

        return stack;
    }
}
