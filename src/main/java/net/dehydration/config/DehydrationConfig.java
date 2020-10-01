package net.dehydration.config;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;

@Config(name = "dehydration")
@Config.Gui.Background("minecraft:textures/block/stone.png")
public class DehydrationConfig implements ConfigData {

  public boolean no_armor_debuff = false;
  public float dehydration_damage = 1.0F;
  @ConfigEntry.Gui.PrefixText
  public int dehydration_damage_interval = 400;
  public int dehydration_damage_effect_time = 2400;
  public int dehydration_tick_interval = 200;
  public int cooling_down_interval = 100;
  public int cooling_down_tick_decrease = 400;
  @ConfigEntry.Gui.PrefixText
  public int heat_icon_x = 5;
  public int heat_icon_y = 55;
}