package net.dehydration.init;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.dehydration.config.DehydrationConfig;

public class ConfigInit {
    public static DehydrationConfig CONFIG = new DehydrationConfig();

    public static void init() {
        AutoConfig.register(DehydrationConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(DehydrationConfig.class).getConfig();
    }

}
