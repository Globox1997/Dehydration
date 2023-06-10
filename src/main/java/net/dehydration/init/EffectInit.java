package net.dehydration.init;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.dehydration.effect.*;

public class EffectInit {

    public final static StatusEffect THIRST = new ThirstEffect(StatusEffectCategory.HARMFUL, 3062757);
    public final static StatusEffect HYDRATION = new HydrationEffect(StatusEffectCategory.BENEFICIAL, 3062757);

    public static void init() {
        Registry.register(Registries.STATUS_EFFECT, new Identifier("dehydration", "thirst_effect"), THIRST);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("dehydration", "hydration_effect"), HYDRATION);
    }

}
