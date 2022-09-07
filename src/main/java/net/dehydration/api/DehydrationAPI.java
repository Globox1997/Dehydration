package net.dehydration.api;

import net.dehydration.access.ThirstManagerAccess;
import net.dehydration.compat.CroptopiaCompat;
import net.dehydration.thirst.ThirstManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

/**
 * The {@link DehydrationAPI} interface, when implemented, allows you to access the main functions provided by the API. <br>
 * The approach is very similar to ModMenu's API, with its {@code ModMenuApi} interface.
 */
public interface DehydrationAPI {
    /**
     * This method registers a listener to the {@link DrinkEvent} your mod will be using. <br>
     * It's available for you to inherit in case you want to do something before/after the listener is registered.
     */
    default void registerDrinkEvent() {
        DrinkEvent.EVENT.register(this::onDrink);
    }

    /**
     * This method allows you to customize what happens when something is drunk. <br>
     * The default implementation gets the {@link ThirstManager}, runs {@link DehydrationAPI#calculateDrinkThirst} and applies that thirst
     *
     * @param stack        The {@link ItemStack} of the {@link DrinkItem} being consumed
     * @param playerEntity The {@link PlayerEntity} consuming the {@link DrinkItem}
     */
    default void onDrink(ItemStack stack, PlayerEntity playerEntity) {
        // Get the ThirstManager using a convenient utility called ThirstManagerAccess
        ThirstManager thirstManager = ((ThirstManagerAccess) playerEntity).getThirstManager();

        // Calculate the thirst using calculateDrinkThirst
        int thirst = calculateDrinkThirst(stack, playerEntity);
        thirstManager.add(thirst);
    }

    /**
     * Calculates the thirst that you get from a specific {@link DrinkItem}. <br>
     * The usual approach for implementing this method would be by using Minecraft tags, like in {@link CroptopiaCompat}. <br>
     * You are free to handle this however you want.
     *
     * @param stack        The consumed {@link DrinkItem}'s {@link ItemStack}
     * @param playerEntity The consumer {@link PlayerEntity}
     * @return Calculated thirst
     */
    int calculateDrinkThirst(ItemStack stack, PlayerEntity playerEntity);
}
