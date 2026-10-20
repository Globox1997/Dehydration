package net.dehydration.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.dehydration.init.TagInit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

@Mixin(FlowableFluid.class)
public abstract class FlowableFluidMixin {

    @Shadow
    protected static int getBlockStateLevel(FluidState state) {
        return 0;
    }

    @Inject(method = "flow", at = @At("HEAD"), cancellable = true)
    private void flowMixin(WorldAccess world, BlockPos pos, BlockState state, Direction direction, FluidState fluidState, CallbackInfo info) {
        if ((Object) this instanceof WaterFluid && state.getFluidState().isIn(TagInit.PURIFIED_WATER)) {
            world.setBlockState(pos, fluidState.getBlockState().with(FluidBlock.LEVEL, getBlockStateLevel(state.getFluidState())), Block.NOTIFY_ALL);
            info.cancel();
        }
    }
}
