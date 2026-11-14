package net.dehydration.init;

import net.dehydration.DehydrationMain;
import net.dehydration.data.DataLoader;
import net.dehydration.network.ThirstServerPacket;
import net.minecraft.server.MinecraftServer;

public class JsonReaderInit {

    public static final DataLoader DATA_LOADER = new DataLoader();

    public static void onDataPackReload(MinecraftServer server, boolean success) {
        if (success) {
            for (int i = 0; i < server.getPlayerList().getPlayers().size(); i++)
                ThirstServerPacket.writeS2CHydrationTemplateSyncPacket(server.getPlayerList().getPlayers().get(i));
            DehydrationMain.LOGGER.info("Finished reload on {}", Thread.currentThread());
        } else
            DehydrationMain.LOGGER.error("Failed to reload on {}", Thread.currentThread());
    }
}
