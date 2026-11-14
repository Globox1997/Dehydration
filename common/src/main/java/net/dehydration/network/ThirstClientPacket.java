package net.dehydration.network;

import java.util.ArrayList;
import java.util.List;

import net.dehydration.DehydrationMain;
import net.dehydration.access.ThirstManagerAccess;
import net.dehydration.api.HydrationTemplate;
import net.dehydration.network.packet.ExcludedThirstPacket;
import net.dehydration.network.packet.HydrationTemplatePacket;
import net.dehydration.network.packet.ThirstPacket;
import net.dehydration.thirst.ThirstManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public class ThirstClientPacket {

    public static void handleThirst(ThirstPacket payload) {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null && client.player.level().getEntity(payload.playerId()) instanceof Player playerEntity) {
            ThirstManager thirstManager = ((ThirstManagerAccess) playerEntity).getThirstManager();
            thirstManager.setThirstLevel(payload.thirstLevel());
        }
    }

    public static void handleExcludedThirst(ExcludedThirstPacket payload) {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null && client.player.level().getEntity(payload.playerId()) instanceof Player playerEntity) {
            ((ThirstManagerAccess) playerEntity).getThirstManager().setThirst(payload.excludingThirst());
        }
    }

    public static void handleHydrationTemplates(HydrationTemplatePacket payload) {
        List<HydrationTemplate> hydrationTemplates = new ArrayList<HydrationTemplate>();
        List<Integer> templateList = payload.templateList();

        int count = 0;
        for (int i = 0; i < templateList.size(); i += 2) {
            List<Item> items = new ArrayList<Item>();
            for (int u = 0; u < templateList.get(i + 1); u++) {
                items.add(BuiltInRegistries.ITEM.getValue(payload.templateIdentifiers().get(count)));
                count++;
            }
            hydrationTemplates.add(new HydrationTemplate(templateList.get(i), items));
        }
        DehydrationMain.CLIENT_HYDRATION_TEMPLATES.clear();
        DehydrationMain.CLIENT_HYDRATION_TEMPLATES.addAll(hydrationTemplates);
    }

}
