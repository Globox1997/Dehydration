package net.dehydration.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.dehydration.fluid.PurifiedWaterFluid;
import net.dehydration.init.FluidInit;
import net.dehydration.init.TagInit;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

@Mixin(WaterFluid.class)
public abstract class WaterFluidMixin extends FlowableFluid {

    private static final boolean isCreateLoaded = FabricLoader.getInstance().isModLoaded("create");

    @Override
    protected void flow(WorldAccess world, BlockPos pos, BlockState state, Direction direction, FluidState fluidState) {
        if (state.getFluidState().isIn(TagInit.PURIFIED_WATER)) {
            world.setBlockState(pos, fluidState.getBlockState().with(FluidBlock.LEVEL, PurifiedWaterFluid.getBlockStateLevel(state.getFluidState())), Block.NOTIFY_ALL);
        } else {
            super.flow(world, pos, state, direction, fluidState);
        }
    }

    @Inject(method = "matchesType", at = @At("HEAD"), cancellable = true)
    private void matchesTypeMixin(Fluid fluid, CallbackInfoReturnable<Boolean> info) {
        if (!isCreateLoaded && (fluid == FluidInit.PURIFIED_WATER || fluid == FluidInit.PURIFIED_FLOWING_WATER)) {
            info.setReturnValue(true);
        }
    }
}
