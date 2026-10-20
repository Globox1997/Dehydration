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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;

@Environment(EnvType.CLIENT)
public class ThirstClientPacket {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(ThirstPacket.PACKET_ID, (payload, context) -> {
            int playerId = payload.playerId();
            int thirstLevel = payload.thirstLevel();
            context.client().execute(() -> {
                if (context.player().getWorld().getEntityById(playerId) instanceof PlayerEntity playerEntity) {
                    ThirstManager thirstManager = ((ThirstManagerAccess) playerEntity).getThirstManager();
                    thirstManager.setThirstLevel(thirstLevel);
                }
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(ExcludedThirstPacket.PACKET_ID, (payload, context) -> {
            int playerId = payload.playerId();
            boolean excludedThirst = payload.excludingThirst();
            context.client().execute(() -> {
                if (context.player().getWorld().getEntityById(playerId) instanceof PlayerEntity playerEntity) {
                    ((ThirstManagerAccess) playerEntity).getThirstManager().setThirst(excludedThirst);
                }
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(HydrationTemplatePacket.PACKET_ID, (payload, context) -> {
            List<HydrationTemplate> hydrationTemplates = new ArrayList<HydrationTemplate>();
            List<Integer> templateList = payload.templateList();

            int count = 0;
            for (int i = 0; i < templateList.size(); i += 2) {
                List<Item> items = new ArrayList<Item>();
                for (int u = 0; u < templateList.get(i + 1); u++) {
                    items.add(Registries.ITEM.get(payload.templateIdentifiers().get(count)));
                    count++;
                }
                hydrationTemplates.add(new HydrationTemplate(templateList.get(i), items));
            }
            context.client().execute(() -> {
                DehydrationMain.CLIENT_HYDRATION_TEMPLATES.clear();
                DehydrationMain.CLIENT_HYDRATION_TEMPLATES.addAll(hydrationTemplates);
            });
        });
    }
}
