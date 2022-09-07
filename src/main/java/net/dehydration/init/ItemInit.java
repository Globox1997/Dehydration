package net.dehydration.init;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.dehydration.item.Leather_Flask;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.potion.Potion;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemInit {
    // Map and List
    private static final Map<Identifier, Item> ITEMS = new LinkedHashMap<>();
    public static final List<Item> FLASK_ITEM_LIST = new ArrayList<Item>();
    // Flasks
    public static final Leather_Flask LEATHER_FLASK = register("leather_flask", new Leather_Flask(0, new Item.Settings().group(ItemGroup.MISC).maxCount(1)));
    public static final Leather_Flask IRON_LEATHER_FLASK = register("iron_leather_flask", new Leather_Flask(1, new Item.Settings().group(ItemGroup.MISC).maxCount(1)));
    public static final Leather_Flask GOLDEN_LEATHER_FLASK = register("golden_leather_flask", new Leather_Flask(2, new Item.Settings().group(ItemGroup.MISC).maxCount(1)));
    public static final Leather_Flask DIAMOND_LEATHER_FLASK = register("diamond_leather_flask", new Leather_Flask(3, new Item.Settings().group(ItemGroup.MISC).maxCount(1)));
    public static final Leather_Flask NETHERITE_LEATHER_FLASK = register("netherite_leather_flask", new Leather_Flask(4, new Item.Settings().group(ItemGroup.MISC).maxCount(1).fireproof()));
    // Potion
    public static final Potion PURIFIED_WATER = new Potion(new StatusEffectInstance[0]);

    private static <I extends Item> I register(String name, I item) {
        ITEMS.put(new Identifier("dehydration", name), item);
        if (name.contains("flask"))
            FLASK_ITEM_LIST.add(item);

        return item;
    }

    public static void init() {
        for (Identifier id : ITEMS.keySet()) {
            Registry.register(Registry.ITEM, id, ITEMS.get(id));
        }
        Registry.register(Registry.POTION, "purified_water", PURIFIED_WATER);
    }

}
