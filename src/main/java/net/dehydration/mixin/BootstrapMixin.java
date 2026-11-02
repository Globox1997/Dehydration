package net.dehydration.mixin;

import net.dehydration.block.entity.RainwaterCollectorBehavior;
import net.minecraft.server.Bootstrap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.dehydration.block.entity.CauldronBehaviorAccess;
import net.dehydration.block.entity.CopperCauldronBehavior;
import net.dehydration.block.entity.DispenserBehaviorAccess;

@Mixin(Bootstrap.class)
public class BootstrapMixin {

    @Inject(method = "bootStrap", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/dispenser/DispenseItemBehavior;bootStrap()V", shift = Shift.AFTER))
    private static void initializeMixin(CallbackInfo info) {
        DispenserBehaviorAccess.registerDefaults();
        CauldronBehaviorAccess.registerBehavior();
        CopperCauldronBehavior.registerBehavior();
        RainwaterCollectorBehavior.registerBehavior();
    }
}