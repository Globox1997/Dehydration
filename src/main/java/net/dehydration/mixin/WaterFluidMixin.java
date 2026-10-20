package net.dehydration.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.dehydration.init.FluidInit;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.WaterFluid;

@Mixin(WaterFluid.class)
public abstract class WaterFluidMixin {

    @Unique
    private static final boolean isCreateLoaded = FabricLoader.getInstance().isModLoaded("create");

    @Inject(method = "matchesType", at = @At("HEAD"), cancellable = true)
    private void matchesTypeMixin(Fluid fluid, CallbackInfoReturnable<Boolean> info) {
        if (!isCreateLoaded && (fluid == FluidInit.PURIFIED_WATER || fluid == FluidInit.PURIFIED_FLOWING_WATER)) {
            info.setReturnValue(true);
        }
    }
}
