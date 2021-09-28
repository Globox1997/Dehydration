package net.dehydration.network;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ThirstServerPacket {
    public static final Identifier THIRST_UPDATE = new Identifier("dehydration", "thirst_update");
    public static final Identifier EXCLUDED_SYNC = new Identifier("dehydration", "excluded_player_sync");

    public static void init() {
    }

    public static void writeS2CExcludedSyncPacket(ServerPlayerEntity serverPlayerEntity, boolean setThirst) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBoolean(setThirst);
        serverPlayerEntity.networkHandler.sendPacket(new CustomPayloadS2CPacket(EXCLUDED_SYNC, buf));
    }
}
