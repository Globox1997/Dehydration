package net.dehydration;

import net.dehydration.api.DehydrationAPI;
import net.dehydration.api.HydrationTemplate;
import net.dehydration.init.*;
import net.dehydration.network.ThirstServerPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DehydrationMain implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("Dehydration");

    // map for storing attributes before logging into a server
    public static final List<HydrationTemplate> HYDRATION_TEMPLATES = new ArrayList<HydrationTemplate>();

    @Override
    public void onInitialize() {
        BlockInit.init();
        CommandInit.init();
        CompatInit.init();
        ConfigInit.init();
        EffectInit.init();
        ItemInit.init();
        EventInit.init();
        FluidInit.init();
        SoundInit.init();
        TagInit.init();
        ThirstServerPacket.init();
        JsonReaderInit.init();

        FabricLoader.getInstance().getEntrypointContainers("dehydration", DehydrationAPI.class).forEach((entrypoint) -> {
            ModMetadata metadata = entrypoint.getProvider().getMetadata();
            String id = metadata.getId();

            try {
                DehydrationAPI api = entrypoint.getEntrypoint();
                api.registerDrinkEvent();
            } catch (Throwable exception) {
                LOGGER.log(Level.ERROR, "Mod {} is providing a broken DehydrationAPI implementation", id, exception);
            }
        });
    }

}
