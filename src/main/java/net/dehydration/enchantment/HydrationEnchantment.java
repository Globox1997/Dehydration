package net.dehydration.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;

public class HydrationEnchantment extends Enchantment {

  public HydrationEnchantment() {
    super(Rarity.RARE, EnchantmentTarget.ARMOR_CHEST, new EquipmentSlot[] { EquipmentSlot.CHEST });
  }

  @Override
  public int getMaxLevel() {
    return 1;
  }

  @Override
  public boolean canAccept(Enchantment enchantment) {
    return enchantment != Enchantments.BLAST_PROTECTION;
  }

}
