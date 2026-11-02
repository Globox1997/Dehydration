package net.dehydration.network.packet;

import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record HydrationTemplatePacket(List<Integer> templateList, List<ResourceLocation> templateIdentifiers) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<HydrationTemplatePacket> PACKET_ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("dehydration", "hydration_template_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, HydrationTemplatePacket> PACKET_CODEC = StreamCodec.ofMember((value, buf) -> {
        buf.writeCollection(value.templateList, FriendlyByteBuf::writeInt);
        buf.writeCollection(value.templateIdentifiers, FriendlyByteBuf::writeResourceLocation);
    }, buf -> new HydrationTemplatePacket(buf.readList(FriendlyByteBuf::readInt), buf.readList(FriendlyByteBuf::readResourceLocation)));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

}
