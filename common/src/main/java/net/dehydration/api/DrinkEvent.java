package net.dehydration.api;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The {@link DrinkEvent} is invoked whenever a {@link DrinkItem} is drunk and is a place to handle thirst.
 * <br>
 * The actual listeners for this event should be implemented using the {@link DehydrationAPI} interface.
 */
public interface DrinkEvent {
    void onDrink(ItemStack stack, Player playerEntity);

    Event EVENT = new Event();

    final class Event {
        private final List<DrinkEvent> listeners = new CopyOnWriteArrayList<>();
        private final DrinkEvent invoker = (stack, playerEntity) -> listeners.forEach((listener) -> listener.onDrink(stack, playerEntity));

        public void register(DrinkEvent listener) {
            this.listeners.add(listener);
        }

        public DrinkEvent invoker() {
            return this.invoker;
        }
    }
}
