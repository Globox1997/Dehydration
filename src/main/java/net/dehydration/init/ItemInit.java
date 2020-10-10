package net.dehydration.init;

import net.dehydration.item.Leather_Flask;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemInit {
  public static final Leather_Flask LEATHER_FLASK = new Leather_Flask(
      new Item.Settings().group(ItemGroup.MISC).maxCount(1));

  public static void init() {
    Registry.register(Registry.ITEM, new Identifier("dehydration", "leather_flask"), LEATHER_FLASK);
  }

}
