package net.dehydration.init;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class TagInit {

    public static final TagKey<Item> HYDRATING_FOOD = TagKey.of(RegistryKeys.ITEM, new Identifier("dehydration", "hydrating_food"));
    public static final TagKey<Item> STRONGER_HYDRATING_FOOD = TagKey.of(RegistryKeys.ITEM, new Identifier("dehydration", "stronger_hydrating_food"));
    public static final TagKey<Item> HYDRATING_STEW = TagKey.of(RegistryKeys.ITEM, new Identifier("dehydration", "hydrating_stew"));
    public static final TagKey<Item> STRONGER_HYDRATING_STEW = TagKey.of(RegistryKeys.ITEM, new Identifier("dehydration", "stronger_hydrating_stew"));
    public static final TagKey<Item> HYDRATING_DRINKS = TagKey.of(RegistryKeys.ITEM, new Identifier("dehydration", "hydrating_drinks"));
    public static final TagKey<Item> STRONGER_HYDRATING_DRINKS = TagKey.of(RegistryKeys.ITEM, new Identifier("dehydration", "stronger_hydrating_drinks"));

    public static final TagKey<Fluid> PURIFIED_WATER = TagKey.of(RegistryKeys.FLUID, new Identifier("dehydration", "purified_water"));

    public static void init() {
    }

}
