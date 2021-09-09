package net.dehydration.network;

import net.dehydration.access.ThirstManagerAccess;
import net.dehydration.init.ConfigInit;
import net.dehydration.thirst.ThirstManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;

public class ThirstClientPacket {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(ThirstServerPacket.THIRST_UPDATE, (client, handler, buffer, responseSender) -> {
            int[] bufferArray = buffer.readIntArray();
            int entityId = bufferArray[0];
            int thirstLevel = bufferArray[1];
            client.execute(() -> {
                if (client.player.world.getEntityById(entityId) != null) {
                    PlayerEntity player = (PlayerEntity) client.player.world.getEntityById(entityId);
                    ThirstManager thirstManager = ((ThirstManagerAccess) player).getThirstManager(player);
                    thirstManager.setThirstLevel(thirstLevel);
                }
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(ThirstServerPacket.EXCLUDED_SYNC, (client, handler, buffer, responseSender) -> {
            client.execute(() -> {
                if (client.player != null) {
                    if (!ConfigInit.CONFIG.excluded_names.contains(client.player.getName().asString())) {
                        ConfigInit.CONFIG.excluded_names.add(client.player.getName().asString());
                    }
                }
            });
        });
    }
}
