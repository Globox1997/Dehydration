package net.dehydration.init;

import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class TagInit {

  public static final Tag<Item> ALLOWED_ARMOR = TagRegistry.item(new Identifier("dehydration", "allowed_armor"));

  public static void init() {
  }

}
