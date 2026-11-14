package net.dehydration;

import net.dehydration.api.DehydrationAPI;
import net.dehydration.api.HydrationTemplate;
import net.dehydration.block.entity.CauldronBehaviorAccess;
import net.dehydration.block.entity.CopperCauldronBehavior;
import net.dehydration.block.entity.DispenserBehaviorAccess;
import net.dehydration.block.entity.RainwaterCollectorBehavior;
import net.dehydration.init.*;
import net.minecraft.resources.Identifier;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DehydrationMain {

    public static final String MOD_ID = "dehydration";

    public static final Logger LOGGER = LogManager.getLogger("Dehydration");

    // map for storing attributes before logging into a server
    public static final List<HydrationTemplate> HYDRATION_TEMPLATES = new ArrayList<HydrationTemplate>();
    public static final List<HydrationTemplate> CLIENT_HYDRATION_TEMPLATES = new ArrayList<HydrationTemplate>();

    public static void init() {
        ConfigInit.init();
    }

    public static void registerBehaviors() {
        DispenserBehaviorAccess.registerDefaults();
        CauldronBehaviorAccess.registerBehavior();
        CopperCauldronBehavior.registerBehavior();
        RainwaterCollectorBehavior.registerBehavior();
    }

    public static void registerApi(String modId, DehydrationAPI api) {
        try {
            api.registerDrinkEvent();
            api.registerFluidBehaviors();
        } catch (Throwable exception) {
            LOGGER.log(Level.ERROR, "Mod {} is providing a broken DehydrationAPI implementation", modId, exception);
        }
    }

    public static Identifier identifierOf(String name) {
        return Identifier.fromNamespaceAndPath(MOD_ID, name);
    }

}
