package net.dehydration.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "dehydration")
@Config.Gui.Background("minecraft:textures/block/stone.png")
public class DehydrationConfig implements ConfigData {

    @Comment("Defines the damage taken every 4 seconds")
    public float thirst_damage = 1.0F;
    @Comment("Defines speed of dehydration, bigger value = slower depletion")
    public float hydrating_factor = 1.5F;
    @Comment("Defines the rate of the thirst effect's drain, smaller variable = less draining")
    public float thirst_effect_factor = 0.05F;
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
    public int honey_quench = 1;
    @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
    public int water_bowl_quench = 3;
    @Comment("1.0F = 100%, 0.5F = 50%, 0.0F = 0%")
    public float water_bowl_thirst_chance = 0.4F;
    @Comment("Applies on items added to the drinks tag")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
    public int drinks_thirst_quench = 2;
    @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
    public int stew_thirst_quench = 3;
    @Comment("Applies to only food tagged with: #hydrating_food")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
    public int food_thirst_quench = 1;
    @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
    public int stronger_drinks_thirst_quench = 4;
    @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
    public int stronger_stew_thirst_quench = 6;
    @Comment("Applies to only food tagged with: #hydrating_food")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
    public int stronger_food_thirst_quench = 2;
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
    public boolean allow_non_flowing_water_sip = false;
    @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
    public int sleep_thirst_consumption = 4;
    @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
    public int sleep_hunger_consumption = 2;
    @Comment("boiling time in ticks")
    public int water_boiling_time = 100;
    @Comment("pump cooldown in ticks 0 = no cooldown")
    public int pump_cooldown = 1200;
    @Comment("Pump requires water below 10 blocks within 50 blocks")
    public boolean pump_requires_water = false;
    public boolean bottle_consumes_source_block = false;
    @Comment("Haste and fatique on low hydration")
    public boolean special_effects = false;

    @Comment("Applies to all ultrawarm dimensions")
    public boolean harder_nether = false;
    @Comment("1.3 = 30% more thirst")
    public float nether_factor = 1.3F;

    @ConfigEntry.Category("advanced_settings")
    @Comment("Enables alternate textures by the lead texture artist")
    public boolean other_droplet_texture = false;
    @ConfigEntry.Category("advanced_settings")
    public int hud_x = 0;
    @ConfigEntry.Category("advanced_settings")
    public int hud_y = 0;
    @ConfigEntry.Category("advanced_settings")
    public boolean thirst_preview = true;
}
