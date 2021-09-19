package net.dehydration.init;

import net.dehydration.compat.CroptopiaCompat;
import net.fabricmc.loader.api.FabricLoader;

public class CompatInit {
    
    public static boolean origins_enabled;

    public static void init() {
        ifLoaded("croptopia", CroptopiaCompat::init);
        
        origins_enabled = (FabricLoader.getInstance().isModLoaded("origins"));
    }

    private static void ifLoaded(String mod, Action action) {
        if (FabricLoader.getInstance().isModLoaded(mod)) action.act();
    }

    @FunctionalInterface
    private interface Action {
        void act();
    }
}
