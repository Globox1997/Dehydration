package net.dehydration.effect;

import net.dehydration.access.ThirstManagerAccess;
import net.dehydration.init.ConfigInit;
import net.dehydration.thirst.ThirstManager;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class ThirstEffect extends MobEffect {

    public ThirstEffect(MobEffectCategory type, int color) {
        super(type, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide() && entity instanceof Player playerEntity) {
            ThirstManager thirstManager = ((ThirstManagerAccess) playerEntity).getThirstManager();
            thirstManager.addDehydration(ConfigInit.CONFIG.thirst_effect_factor * (float) (amplifier + 1));
        }
        return super.applyEffectTick(entity, amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

}
