package net.dehydration.network;

import net.dehydration.access.ThristManagerAccess;
import net.dehydration.thirst.ThirstManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class ThirstUpdateS2CPacket {
    public static final Identifier THIRST_UPDATE = new Identifier("dehydration", "thirst_update");

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(THIRST_UPDATE, (client, handler, buffer, responseSender) -> {
            int[] bufferArray = buffer.readIntArray();
            int entityId = bufferArray[0];
            int thirstLevel = bufferArray[1];
            client.execute(() -> {
                if (client.player.world.getEntityById(entityId) != null) {
                    PlayerEntity player = (PlayerEntity) client.player.world.getEntityById(entityId);
                    ThirstManager thirstManager = ((ThristManagerAccess) player).getThirstManager(player);
                    thirstManager.setThirstLevel(thirstLevel);
                }
            });
        });

    }
}