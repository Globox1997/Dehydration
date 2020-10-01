package net.dehydration.init;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import net.dehydration.config.DehydrationConfig;

public class ConfigInit {
  public static DehydrationConfig CONFIG = new DehydrationConfig();

  public static void init() {
    AutoConfig.register(DehydrationConfig.class, JanksonConfigSerializer::new);
    CONFIG = AutoConfig.getConfigHolder(DehydrationConfig.class).getConfig();
  }

}
