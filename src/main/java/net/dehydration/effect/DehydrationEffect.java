package net.dehydration.effect;

import java.util.UUID;

import net.dehydration.access.ThristManagerAccess;
import net.dehydration.init.ConfigInit;
import net.dehydration.init.TagInit;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class DehydrationEffect extends StatusEffect {
  private final UUID DEHYDRATION = UUID.fromString("80e24bea-844e-4944-a36a-edb66e841e66");

  public DehydrationEffect(StatusEffectType type, int color) {
    super(type, color);
  }

  @Override
  public void applyUpdateEffect(LivingEntity entity, int amplifier) {
    DamageSource damageSource = createDamageSource();
    entity.damage(damageSource, ConfigInit.CONFIG.dehydration_damage);
    if (entity instanceof PlayerEntity) {
      PlayerEntity player = (PlayerEntity) entity;
      player.addExhaustion(0.01F);
      ((ThristManagerAccess) player).getThirstManager(player).addDehydration(0.5F);
    }
  }

  @Override
  public boolean canApplyUpdateEffect(int duration, int amplifier) {
    return duration % ConfigInit.CONFIG.dehydration_damage_interval == 0;
  }

  @Override
  public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
    EntityAttributeInstance entityAttributeInstance = attributes
        .getCustomInstance((EntityAttributes.GENERIC_MOVEMENT_SPEED));
    if (entityAttributeInstance != null) {
      EntityAttributeModifier entityAttributeModifier = new EntityAttributeModifier(this.getTranslationKey(), -0.15D,
          EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
      entityAttributeInstance.removeModifier(entityAttributeModifier);
      entityAttributeInstance.addPersistentModifier(
          new EntityAttributeModifier(DEHYDRATION, this.getTranslationKey() + " " + entityAttributeModifier.getValue(),
              this.adjustModifierAmount(amplifier, entityAttributeModifier), entityAttributeModifier.getOperation()));
    }

  }

  @Override
  public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
    EntityAttributeInstance entityAttributeInstance = attributes
        .getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
    if (entityAttributeInstance != null) {
      entityAttributeInstance.removeModifier(DEHYDRATION);
    }

  }

  public static DamageSource createDamageSource() {
    return new EntityDamageSource("dehydration", null);
  }

  public static int wearsArmorModifier(LivingEntity livingEntity) {
    int wearsArmorModifier = ConfigInit.CONFIG.wears_armor_modifier;
    boolean noArmorDebuff = ConfigInit.CONFIG.no_armor_debuff;
    int warmingModifier = 0;
    ItemStack headStack = livingEntity.getEquippedStack(EquipmentSlot.HEAD);
    ItemStack chestStack = livingEntity.getEquippedStack(EquipmentSlot.CHEST);
    ItemStack legStack = livingEntity.getEquippedStack(EquipmentSlot.LEGS);
    ItemStack feetStack = livingEntity.getEquippedStack(EquipmentSlot.FEET);

    if (!noArmorDebuff) {
      if (headStack.isEmpty() || headStack.getItem().isIn(TagInit.ALLOWED_ARMOR)) {
        warmingModifier = warmingModifier + wearsArmorModifier;
      }
      if (chestStack.isEmpty() || chestStack.getItem().isIn(TagInit.ALLOWED_ARMOR)) {
        warmingModifier = warmingModifier + wearsArmorModifier;
      }
      if (legStack.isEmpty() || legStack.getItem().isIn(TagInit.ALLOWED_ARMOR)) {
        warmingModifier = warmingModifier + wearsArmorModifier;
      }
      if (feetStack.isEmpty() || feetStack.getItem().isIn(TagInit.ALLOWED_ARMOR)) {
        warmingModifier = warmingModifier + wearsArmorModifier;
      }
      return warmingModifier;
    } else
      return wearsArmorModifier * 4;
  }

}
