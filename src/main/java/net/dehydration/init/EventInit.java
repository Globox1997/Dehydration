package net.dehydration.init;

import net.dehydration.access.ServerPlayerAccess;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;

public class EventInit {

    public static void init() {
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
            ((ServerPlayerAccess) player).compatSync();
        });
    }
}
