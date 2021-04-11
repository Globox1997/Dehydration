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
  @Comment("Defines the damage taken every 4 seconds")
  public float thirst_damage = 1.0F;
  @Comment("Defines speed of dehydration, bigger value = slower depletion")
  public float hydrating_factor = 1.5F;
  @Comment("Defines the rate of the thirst effect's drain, smaller variable = less draining")
  public float thirst_effect_factor = 0.033F;
  @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
  public int potion_thirst_quench = 2;
  @Comment("1.0F = 100%, 0.5F = 50%, 0.0F = 0%")
  public float potion_bad_thirst_chance = 0.25F;
  @Comment("Counted in ticks, 20 ticks = 1 second")
  public int potion_bad_thirst_duration = 300;
  @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
  public int milk_thirst_quench = 8;
  @Comment("1.0F = 100%, 0.5F = 50%, 0.0F = 0%")
  public float milk_thirst_chance = 0.4F;
  @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
  public int stew_thirst_quench = 3;
  @Comment("Applies to only food tagged with: #hydrating_food")
  @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
  public int food_thirst_quench = 1;
  @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
  public int flask_thirst_quench = 4;
  @Comment("1.0F = 100%, 0.5F = 50%, 0.0F = 0%")
  public float flask_dirty_thirst_chance = 0.75F;
  @Comment("Counted in ticks, 20 ticks = 1 second")
  public int flask_dirty_thirst_duration = 500;
  @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
  public int water_souce_quench = 1;
  @Comment("1.0F = 100%, 0.5F = 50%, 0.0F = 0%")
  public float water_sip_thirst_chance = 0.75F;
  @Comment("Counted in ticks, 20 ticks = 1 second")
  public int water_sip_thirst_duration = 600;
  @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
  public int sleep_thirst_consumption = 4;
  @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
  public int sleep_hunger_consumption = 2;
  @Comment("Enables alternate textures by the lead texture artist")
  public boolean other_droplet_texture = false;
  @Comment("Players listed here won't have thirst")
  public List<String> excluded_names = new ArrayList<>();
}
