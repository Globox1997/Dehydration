package net.dehydration.mixin;

import java.util.function.Predicate;

import net.dehydration.block.AbstractRainwaterCollectorBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.sugar.Local;

import net.dehydration.block.AbstractCopperCauldronBlock;
import net.dehydration.block.CampfireCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

@Mixin(PointedDripstoneBlock.class)
public abstract class PointedDripstoneBlockMixin extends Block {
    public PointedDripstoneBlockMixin(Properties settings) {
        super(settings);
    }

    @ModifyVariable(method = "findFillableCauldronBelowStalactiteTip", at = @At("STORE"), ordinal = 0)
    private static Predicate<BlockState> canBeFilledByDripstoneMixin(Predicate<BlockState> predicate, @Local Fluid fluid) {
        return predicate.or(state -> state.getBlock() instanceof AbstractCopperCauldronBlock
                        && ((AbstractCopperCauldronBlock) state.getBlock()).canBeFilledByDripstone(fluid))
                .or(state -> state.getBlock() instanceof CampfireCauldronBlock
                        && ((CampfireCauldronBlock) state.getBlock()).canBeFilledByDripstone(fluid))
                .or(state -> state.getBlock() instanceof AbstractRainwaterCollectorBlock
                        && ((AbstractRainwaterCollectorBlock) state.getBlock()).canBeFilledByDripstone(fluid));
    }
}
