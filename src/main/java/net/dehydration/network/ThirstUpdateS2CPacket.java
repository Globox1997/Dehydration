package net.dehydration.network;

import net.dehydration.access.ThristManagerAccess;
import net.dehydration.thirst.ThirstManager;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class ThirstUpdateS2CPacket {
  public static final Identifier THIRST_UPDATE = new Identifier("dehydration", "thirst_update");

  public static void init() {
    ClientSidePacketRegistry.INSTANCE.register(THIRST_UPDATE, (context, buffer) -> {
      int[] bufferArray = buffer.readIntArray();
      int entityId = bufferArray[0];
      int thirstLevel = bufferArray[1];
      context.getTaskQueue().execute(() -> {
        if (context.getPlayer().world.getEntityById(entityId) != null) {
          PlayerEntity player = (PlayerEntity) context.getPlayer().world.getEntityById(entityId);
          ThirstManager thirstManager = ((ThristManagerAccess) player).getThirstManager(player);
          thirstManager.setThirstLevel(thirstLevel);
        }
      });
    });
  }

}