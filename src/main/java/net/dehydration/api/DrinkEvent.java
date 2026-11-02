package net.dehydration.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import java.util.Arrays;

/**
 * The {@link DrinkEvent} is invoked whenever a {@link DrinkItem} is drunk and is a place to handle thirst.
 * <br>
 * The actual listeners for this event should be implemented using the {@link DehydrationAPI} interface.
 */
public interface DrinkEvent {
    void onDrink(ItemStack stack, Player playerEntity);

    Event<DrinkEvent> EVENT = EventFactory.createArrayBacked(DrinkEvent.class, (listeners) ->
        (stack, playerEntity) -> {
            Arrays.stream(listeners).forEach((listener) -> listener.onDrink(stack, playerEntity));
        });
}
