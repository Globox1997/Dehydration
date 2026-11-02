package net.dehydration.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.dehydration.init.TagInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.WaterFluid;

@Mixin(FlowingFluid.class)
public abstract class FlowableFluidMixin {

    @Shadow
    protected static int getLegacyLevel(FluidState state) {
        return 0;
    }

    @Inject(method = "spreadTo", at = @At("HEAD"), cancellable = true)
    private void flowMixin(LevelAccessor world, BlockPos pos, BlockState state, Direction direction, FluidState fluidState, CallbackInfo info) {
        if ((Object) this instanceof WaterFluid && state.getFluidState().is(TagInit.PURIFIED_WATER)) {
            world.setBlock(pos, fluidState.createLegacyBlock().setValue(LiquidBlock.LEVEL, getLegacyLevel(state.getFluidState())), Block.UPDATE_ALL);
            info.cancel();
        }
    }
}
