package net.dehydration.init;

import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TagInit {

    public static final TagKey<Item> HYDRATING_FOOD = TagKey.of(Registry.ITEM_KEY, new Identifier("dehydration", "hydrating_food"));
    public static final TagKey<Item> STRONGER_HYDRATING_FOOD = TagKey.of(Registry.ITEM_KEY, new Identifier("dehydration", "stronger_hydrating_food"));
    public static final TagKey<Item> HYDRATING_STEW = TagKey.of(Registry.ITEM_KEY, new Identifier("dehydration", "hydrating_stew"));
    public static final TagKey<Item> STRONGER_HYDRATING_STEW = TagKey.of(Registry.ITEM_KEY, new Identifier("dehydration", "stronger_hydrating_stew"));
    public static final TagKey<Item> HYDRATING_DRINKS = TagKey.of(Registry.ITEM_KEY, new Identifier("dehydration", "hydrating_drinks"));
    public static final TagKey<Item> STRONGER_HYDRATING_DRINKS = TagKey.of(Registry.ITEM_KEY, new Identifier("dehydration", "stronger_hydrating_drinks"));

    public static void init() {
    }

}
