package net.dehydration.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ThirstServerPacket {
    public static final Identifier THIRST_UPDATE = new Identifier("dehydration", "thirst_update");
    public static final Identifier EXCLUDED_SYNC = new Identifier("dehydration", "excluded_player_sync");

    public static void init() {
    }

    public static void writeS2CExcludedSyncPacket(ServerPlayerEntity serverPlayerEntity) {
        ServerPlayNetworking.send(serverPlayerEntity, EXCLUDED_SYNC, new PacketByteBuf(Unpooled.buffer()));
    }
}
