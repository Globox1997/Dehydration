package net.dehydration.init;

import net.dehydration.DehydrationMain;
import net.dehydration.data.DataLoader;
import net.dehydration.network.ThirstServerPacket;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;

public class JsonReaderInit {

    public static void init() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new DataLoader());
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, serverResourceManager, success) -> {
            if (success) {
                for (int i = 0; i < server.getPlayerManager().getPlayerList().size(); i++)
                    ThirstServerPacket.writeS2CHydrationTemplateSyncPacket(server.getPlayerManager().getPlayerList().get(i));
                DehydrationMain.LOGGER.info("Finished reload on {}", Thread.currentThread());
            } else
                DehydrationMain.LOGGER.error("Failed to reload on {}", Thread.currentThread());
        });
    }
}
