package net.dehydration.init;

import net.dehydration.DehydrationMain;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

public class TagInit {

    public static final TagKey<Item> HYDRATING_FOOD = TagKey.create(Registries.ITEM, DehydrationMain.identifierOf("hydrating_food"));
    public static final TagKey<Item> STRONGER_HYDRATING_FOOD = TagKey.create(Registries.ITEM, DehydrationMain.identifierOf("stronger_hydrating_food"));
    public static final TagKey<Item> HYDRATING_STEW = TagKey.create(Registries.ITEM, DehydrationMain.identifierOf("hydrating_stew"));
    public static final TagKey<Item> STRONGER_HYDRATING_STEW = TagKey.create(Registries.ITEM, DehydrationMain.identifierOf("stronger_hydrating_stew"));
    public static final TagKey<Item> HYDRATING_DRINKS = TagKey.create(Registries.ITEM, DehydrationMain.identifierOf("hydrating_drinks"));
    public static final TagKey<Item> STRONGER_HYDRATING_DRINKS = TagKey.create(Registries.ITEM, DehydrationMain.identifierOf("stronger_hydrating_drinks"));

    public static final TagKey<Fluid> PURIFIED_WATER = TagKey.create(Registries.FLUID, DehydrationMain.identifierOf("purified_water"));

    public static void init() {
    }

}
