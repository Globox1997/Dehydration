package net.dehydration.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(StatusEffect.class)
public class StatusEffectMixin {

    @Redirect(method = "applyUpdateEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addExhaustion(F)V"))
    private void applyUpdateEffectMixin(PlayerEntity playerEntity, float f) {
        playerEntity.getHungerManager().addExhaustion(f);
    }

}