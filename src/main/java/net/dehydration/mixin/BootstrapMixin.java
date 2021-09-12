package net.dehydration.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.dehydration.block.entity.CopperCauldronBehavior;
import net.dehydration.block.entity.DispenserBehaviorAccess;
import net.minecraft.Bootstrap;

@Mixin(Bootstrap.class)
public class BootstrapMixin {

    @Inject(method = "initialize", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/dispenser/DispenserBehavior;registerDefaults()V", shift = Shift.AFTER))
    private static void initializeMixin(CallbackInfo info) {
        DispenserBehaviorAccess.registerDefaults();
        CopperCauldronBehavior.registerBehavior();
    }
}