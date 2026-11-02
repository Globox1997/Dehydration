package net.dehydration.block;

import net.dehydration.block.entity.RainwaterCollectorBehavior;
import net.dehydration.init.BlockInit;
import net.dehydration.init.FluidInit;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class RainwaterCollectorBlock extends AbstractRainwaterCollectorBlock {

    public RainwaterCollectorBlock(BlockBehaviour.Properties settings) {
        super(settings, RainwaterCollectorBehavior.EMPTY_RAINWATER_COLLECTOR_BEHAVIOR);
    }

    @Override
    public boolean isFull(BlockState state) {
        return false;
    }

    public static boolean canFillWithPrecipitation(Level world, Biome.Precipitation precipitation) {
        if (precipitation == Biome.Precipitation.RAIN) {
            return world.getRandom().nextFloat() < 0.6F;
        } else if (precipitation == Biome.Precipitation.SNOW) {
            return world.getRandom().nextFloat() < 0.15F;
        } else {
            return false;
        }
    }

    @Override
    public void handlePrecipitation(BlockState state, Level world, BlockPos pos, Biome.Precipitation precipitation) {
        if (canFillWithPrecipitation(world, precipitation)) {
            if (precipitation == Biome.Precipitation.RAIN) {
                world.setBlockAndUpdate(pos, BlockInit.RAINWATER_PURIFIED_WATER_COLLECTOR_BLOCK.defaultBlockState());
                world.gameEvent(null, GameEvent.FLUID_PLACE, pos);
            } else if (precipitation == Biome.Precipitation.SNOW) {
                world.setBlockAndUpdate(pos, BlockInit.RAINWATER_POWDERED_COLLECTOR_BLOCK.defaultBlockState());
                world.gameEvent(null, GameEvent.FLUID_PLACE, pos);
            }

        }
    }

    @Override
    public boolean canBeFilledByDripstone(Fluid fluid) {
        return fluid == Fluids.WATER || fluid == FluidInit.PURIFIED_WATER;
    }

    @Override
    protected void fillFromDripstone(BlockState state, Level world, BlockPos pos, Fluid fluid) {
        if (fluid == Fluids.WATER || fluid == FluidInit.PURIFIED_WATER) {
            world.setBlockAndUpdate(pos, BlockInit.RAINWATER_PURIFIED_WATER_COLLECTOR_BLOCK.defaultBlockState());
            world.levelEvent(LevelEvent.SOUND_DRIP_WATER_INTO_CAULDRON, pos, 0);
            world.gameEvent(null, GameEvent.FLUID_PLACE, pos);
        }

    }
}
