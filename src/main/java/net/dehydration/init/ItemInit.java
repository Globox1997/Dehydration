package net.dehydration.init;

import java.util.LinkedHashMap;
import java.util.Map;

import net.dehydration.item.Leather_Flask;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemInit {
  private static final Map<Identifier, Item> ITEMS = new LinkedHashMap<>();

  // public static final Leather_Flask LEATHER_FLASK = new Leather_Flask(
  // new Item.Settings().group(ItemGroup.MISC).maxCount(1));

  public static final Leather_Flask LEATHER_FLASK = register("leather_flask",
      new Leather_Flask(0, new Item.Settings().group(ItemGroup.MISC)));
  public static final Leather_Flask IRON_LEATHER_FLASK = register("iron_leather_flask",
      new Leather_Flask(1, new Item.Settings().group(ItemGroup.MISC)));
  public static final Leather_Flask GOLDEN_LEATHER_FLASK = register("golden_leather_flask",
      new Leather_Flask(2, new Item.Settings().group(ItemGroup.MISC)));
  public static final Leather_Flask DIAMOND_LEATHER_FLASK = register("diamond_leather_flask",
      new Leather_Flask(3, new Item.Settings().group(ItemGroup.MISC)));
  public static final Leather_Flask NETHERITE_LEATHER_FLASK = register("netherite_leather_flask",
      new Leather_Flask(4, new Item.Settings().group(ItemGroup.MISC).fireproof()));
  // public static void init() {
  // Registry.register(Registry.ITEM, new Identifier("dehydration",
  // "leather_flask"), LEATHER_FLASK);
  // }

  private static <I extends Item> I register(String name, I item) {
    ITEMS.put(new Identifier("dehydration", name), item);
    return item;
  }

  public static void init() {
    for (Identifier id : ITEMS.keySet()) {
      Registry.register(Registry.ITEM, id, ITEMS.get(id));
    }
  }

}
