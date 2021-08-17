package net.dehydration.init;

import net.dehydration.compat.CroptopiaCompat;
import net.fabricmc.loader.api.FabricLoader;

public class CompatInit {

    public static void init() {
        ifLoaded("croptopia", CroptopiaCompat::init);
    }

    private static void ifLoaded(String mod, Action action) {
        if (FabricLoader.getInstance().isModLoaded(mod)) action.act();
    }

    @FunctionalInterface
    private interface Action {
        void act();
    }
}
