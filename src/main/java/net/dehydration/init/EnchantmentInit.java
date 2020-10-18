package net.dehydration.init;

import net.dehydration.enchantment.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EnchantmentInit {
  public static final Enchantment HYDRATION_ENCHANTMENT = Registry.register(Registry.ENCHANTMENT,
      new Identifier("dehydration", "hydration_enchantment"), new HydrationEnchantment());

  public static void init() {

  }

}
