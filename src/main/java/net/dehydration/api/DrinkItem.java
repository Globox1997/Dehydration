package net.dehydration.api;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

/**
 * The {@link DrinkItem} represents any drinkable item
 */
public class DrinkItem extends Item {
    public DrinkItem(Settings settings) {
        super(settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        PlayerEntity playerEntity = user instanceof PlayerEntity ? (PlayerEntity) user : null;

        // Trigger consumption of an item if it's the server
        if (playerEntity instanceof ServerPlayerEntity) {
            Criteria.CONSUME_ITEM.trigger((ServerPlayerEntity) playerEntity, stack);
        }

        if (playerEntity != null) {
            // Increment the use statistic
            playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));

            // Drinks are not available in creative mode
            if (!playerEntity.getAbilities().creativeMode) {
                // The drink also has to have a FoodComponent
                if (isFood()) {
                    DrinkEvent.EVENT.invoker().onDrink(stack, playerEntity);
                    user.eatFood(world, stack);
                }
            }
        }

        // Create a glass bottle after the item is consumed
        if (playerEntity == null || !playerEntity.getAbilities().creativeMode) {
            if (stack.isEmpty()) {
                return new ItemStack(Items.GLASS_BOTTLE);
            }

            if (playerEntity != null) {
                // offerOrDrop is generally safer to use than insertStack
                playerEntity.getInventory().offerOrDrop(new ItemStack(Items.GLASS_BOTTLE));
            }
        }

        return stack;
    }
}
