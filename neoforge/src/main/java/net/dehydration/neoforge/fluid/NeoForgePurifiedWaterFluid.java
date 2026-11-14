package net.dehydration.neoforge.fluid;

import org.jspecify.annotations.Nullable;

import net.dehydration.fluid.PurifiedWaterFluid;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;

public class NeoForgePurifiedWaterFluid {

    public static final FluidType TYPE = new FluidType(FluidType.Properties.create()
            .descriptionId("block.dehydration.purified_water")
            .fallDistanceModifier(0F)
            .canExtinguish(true)
            .supportsBoating(true)
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
            .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
            .canHydrate(true)
            .isWaterLike(true)) {
        @Override
        public @Nullable PathType getBlockPathType(FluidState state, BlockGetter level, BlockPos pos, @Nullable Mob mob, boolean canFluidLog) {
            return canFluidLog ? super.getBlockPathType(state, level, pos, mob, true) : null;
        }
    };

    public static class Still extends PurifiedWaterFluid.Still {
        @Override
        public FluidType getFluidType() {
            return TYPE;
        }
    }

    public static class Flowing extends PurifiedWaterFluid.Flowing {
        @Override
        public FluidType getFluidType() {
            return TYPE;
        }
    }
}
