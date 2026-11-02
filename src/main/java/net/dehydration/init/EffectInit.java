package net.dehydration.init;

import net.dehydration.effect.*;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class EffectInit {

    public final static Holder<MobEffect> THIRST = register("dehydration:thirst_effect", new ThirstEffect(MobEffectCategory.HARMFUL, 3062757));
    public final static Holder<MobEffect> HYDRATION = register("dehydration:hydration_effect", new HydrationEffect(MobEffectCategory.BENEFICIAL, 3062757));

    public static void init() {
    }

    private static Holder<MobEffect> register(String id, MobEffect statusEffect) {
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, Identifier.parse(id), statusEffect);
    }

}
