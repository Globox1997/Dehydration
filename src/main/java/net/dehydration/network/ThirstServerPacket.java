package net.dehydration.network;

import java.util.ArrayList;
import java.util.List;

import net.dehydration.DehydrationMain;
import net.dehydration.access.ThirstManagerAccess;
import net.dehydration.network.packet.ExcludedThirstPacket;
import net.dehydration.network.packet.HydrationTemplatePacket;
import net.dehydration.network.packet.ThirstPacket;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public class ThirstServerPacket {

    public static void init() {
        PayloadTypeRegistry.clientboundPlay().register(ThirstPacket.PACKET_ID, ThirstPacket.PACKET_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ExcludedThirstPacket.PACKET_ID, ExcludedThirstPacket.PACKET_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(HydrationTemplatePacket.PACKET_ID, HydrationTemplatePacket.PACKET_CODEC);
    }

    public static void writeS2CExcludedSyncPacket(ServerPlayer serverPlayerEntity, boolean setThirst) {
        ServerPlayNetworking.send(serverPlayerEntity, new ExcludedThirstPacket(serverPlayerEntity.getId(), setThirst));
    }

    public static void writeS2CThirstUpdatePacket(ServerPlayer serverPlayerEntity) {
        ServerPlayNetworking.send(serverPlayerEntity, new ThirstPacket(serverPlayerEntity.getId(), ((ThirstManagerAccess) serverPlayerEntity).getThirstManager().getThirstLevel()));
    }

    public static void writeS2CHydrationTemplateSyncPacket(ServerPlayer serverPlayerEntity) {
        List<Integer> templateList = new ArrayList<Integer>();
        DehydrationMain.HYDRATION_TEMPLATES.forEach((template) -> {
            templateList.add(template.getHydration());
            templateList.add(template.getItems().size());
        });
        List<Identifier> templateIdentifiers = new ArrayList<Identifier>();
        DehydrationMain.HYDRATION_TEMPLATES.forEach((template) -> {
            template.getItems().forEach((item) -> {
                templateIdentifiers.add(BuiltInRegistries.ITEM.getKey(item));
            });
        });
        ServerPlayNetworking.send(serverPlayerEntity, new HydrationTemplatePacket(templateList, templateIdentifiers));
    }
}
