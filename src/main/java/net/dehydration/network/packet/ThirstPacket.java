package net.dehydration.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ThirstPacket(int playerId, int thirstLevel) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ThirstPacket> PACKET_ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("dehydration", "thirst_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ThirstPacket> PACKET_CODEC = StreamCodec.ofMember((value, buf) -> {
        buf.writeInt(value.playerId);
        buf.writeInt(value.thirstLevel);
    }, buf -> new ThirstPacket(buf.readInt(), buf.readInt()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

}
