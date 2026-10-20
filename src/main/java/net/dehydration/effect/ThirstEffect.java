package net.dehydration.effect;

import net.dehydration.access.ThirstManagerAccess;
import net.dehydration.init.ConfigInit;
import net.dehydration.thirst.ThirstManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;

public class ThirstEffect extends StatusEffect {

    public ThirstEffect(StatusEffectCategory type, int color) {
        super(type, color);
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (!entity.getWorld().isClient() && entity instanceof PlayerEntity playerEntity) {
            ThirstManager thirstManager = ((ThirstManagerAccess) playerEntity).getThirstManager();
            thirstManager.addDehydration(ConfigInit.CONFIG.thirst_effect_factor * (float) (amplifier + 1));
        }
        return super.applyUpdateEffect(entity, amplifier);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

}
