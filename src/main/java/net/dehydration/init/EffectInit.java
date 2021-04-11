package net.dehydration.init;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.dehydration.effect.DehydrationEffect;
import net.dehydration.effect.ThirstEffect;

public class EffectInit {
  public final static StatusEffect DEHYDRATION = new DehydrationEffect(StatusEffectType.HARMFUL, 16755263);
  public final static StatusEffect THIRST = new ThirstEffect(StatusEffectType.HARMFUL, 3062757);

  public static void init() {
    Registry.register(Registry.STATUS_EFFECT, new Identifier("dehydration", "dehydration_effect"), DEHYDRATION);
    Registry.register(Registry.STATUS_EFFECT, new Identifier("dehydration", "thirst_effect"), THIRST);
  }

}
