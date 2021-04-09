package net.dehydration.config;

import java.util.ArrayList;
import java.util.List;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "dehydration")
@Config.Gui.Background("minecraft:textures/block/stone.png")
public class DehydrationConfig implements ConfigData {
  // @ConfigEntry.Category("hydration_settings")
  public boolean no_armor_debuff = false;
  public float thirst_damage = 1.0F;
  @ConfigEntry.Gui.PrefixText
  public int dehydration_damage_interval = 400;
  public int dehydration_damage_effect_time = 2400;
  @Comment("Time after dehydration occurs")
  public int dehydration_tick_interval = 400;
  public int cooling_down_interval = 100;
  public int cooling_down_tick_decrease = 400;
  @Comment("Defines hydrating rate, higher variable = slower dehydrate")
  public float hydrating_factor = 1.5F;
  public int wears_armor_modifier = 40;
  public int potion_thirst_quench = 3;
  public int milk_thirst_quench = 3;
  public int stew_thirst_quench = 2;
  @Comment("Applies only for food tagged with: hydrating_food")
  public int food_thirst_quench = 1;
  public int flask_thirst_quench = 4;
  @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
  public int sleep_thirst_consumption = 4;
  @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
  public int sleep_hunger_consumption = 2;
  public boolean old_texture = false;
  @Comment("Players listed here won't have thirst")
  public List<String> excluded_names = new ArrayList<>();
}