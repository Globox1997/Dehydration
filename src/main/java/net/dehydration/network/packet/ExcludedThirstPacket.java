package net.dehydration.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ExcludedThirstPacket(int playerId, boolean excludingThirst) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ExcludedThirstPacket> PACKET_ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("dehydration", "excluded_thirst_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ExcludedThirstPacket> PACKET_CODEC = StreamCodec.ofMember((value, buf) -> {
        buf.writeInt(value.playerId);
        buf.writeBoolean(value.excludingThirst);
    }, buf -> new ExcludedThirstPacket(buf.readInt(), buf.readBoolean()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

}
