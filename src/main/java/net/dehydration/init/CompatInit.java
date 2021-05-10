package net.dehydration.init;

import net.dehydration.compat.CroptopiaCompat;
import net.fabricmc.loader.api.FabricLoader;

public class CompatInit {

    public static void init() {
        if (FabricLoader.getInstance().isModLoaded("croptopia")) {
            CroptopiaCompat.init();
        }
    }

}
