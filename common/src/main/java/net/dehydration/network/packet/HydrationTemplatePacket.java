package net.dehydration.network.packet;

import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record HydrationTemplatePacket(List<Integer> templateList, List<Identifier> templateIdentifiers) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<HydrationTemplatePacket> PACKET_ID = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("dehydration", "hydration_template_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, HydrationTemplatePacket> PACKET_CODEC = StreamCodec.composite(ByteBufCodecs.INT.apply(ByteBufCodecs.list()),
            HydrationTemplatePacket::templateList, Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()), HydrationTemplatePacket::templateIdentifiers, HydrationTemplatePacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

}
