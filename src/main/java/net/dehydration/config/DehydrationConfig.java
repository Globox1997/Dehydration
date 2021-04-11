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
  public float thirst_damage = 1.0F;
  @Comment("Defines hydrating rate, bigger variable = slower dehydrate")
  public float hydrating_factor = 1.5F;
  @Comment("Defines thirst effect draining rate, smaller variable = less draining")
  public float thirst_effect_factor = 0.05F;
  @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
  public int potion_thirst_quench = 2;
  @Comment("0.25F = 25%")
  public float potion_bad_thirst_chance = 0.25F;
  @Comment("20ticks = 1second")
  public int potion_bad_thirst_duration = 300;
  @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
  public int milk_thirst_quench = 8;
  @Comment("0.4F = 40%")
  public float milk_thirst_chance = 0.4F;
  @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
  public int stew_thirst_quench = 3;
  @Comment("Applies only for food tagged with: hydrating_food")
  @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
  public int food_thirst_quench = 1;
  @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
  public int flask_thirst_quench = 4;
  public float flask_dirty_thirst_chance = 0.75F;
  @Comment("20ticks = 1second")
  public int flask_dirty_thirst_duration = 500;
  @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
  public int water_souce_quench = 1;
  @Comment("0.75F = 75%")
  public float water_sip_thirst_chance = 0.75F;
  @Comment("20ticks = 1second")
  public int water_sip_thirst_duration = 600;
  @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
  public int sleep_thirst_consumption = 4;
  @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
  public int sleep_hunger_consumption = 2;
  public boolean other_droplet_texture = false;
  @Comment("Players listed here won't have thirst")
  public List<String> excluded_names = new ArrayList<>();
}