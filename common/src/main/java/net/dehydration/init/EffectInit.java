package net.dehydration.init;

import java.util.function.BiConsumer;

import net.dehydration.effect.*;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class EffectInit {

    private static final MobEffect THIRST_EFFECT = new ThirstEffect(MobEffectCategory.HARMFUL, 3062757);
    private static final MobEffect HYDRATION_EFFECT = new HydrationEffect(MobEffectCategory.BENEFICIAL, 3062757);

    public static Holder<MobEffect> THIRST;
    public static Holder<MobEffect> HYDRATION;

    public static void register(BiConsumer<Identifier, MobEffect> registrar) {
        registrar.accept(Identifier.parse("dehydration:thirst_effect"), THIRST_EFFECT);
        registrar.accept(Identifier.parse("dehydration:hydration_effect"), HYDRATION_EFFECT);
        THIRST = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(THIRST_EFFECT);
        HYDRATION = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(HYDRATION_EFFECT);
    }

}
