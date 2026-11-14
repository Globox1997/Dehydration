package net.dehydration.network;

import java.util.ArrayList;
import java.util.List;

import net.dehydration.DehydrationMain;
import net.dehydration.access.ThirstManagerAccess;
import net.dehydration.network.packet.ExcludedThirstPacket;
import net.dehydration.network.packet.HydrationTemplatePacket;
import net.dehydration.network.packet.ThirstPacket;
import net.dehydration.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public class ThirstServerPacket {

    public static void writeS2CExcludedSyncPacket(ServerPlayer serverPlayerEntity, boolean setThirst) {
        Services.network().sendToPlayer(serverPlayerEntity, new ExcludedThirstPacket(serverPlayerEntity.getId(), setThirst));
    }

    public static void writeS2CThirstUpdatePacket(ServerPlayer serverPlayerEntity) {
        Services.network().sendToPlayer(serverPlayerEntity, new ThirstPacket(serverPlayerEntity.getId(), ((ThirstManagerAccess) serverPlayerEntity).getThirstManager().getThirstLevel()));
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
        Services.network().sendToPlayer(serverPlayerEntity, new HydrationTemplatePacket(templateList, templateIdentifiers));
    }
}
