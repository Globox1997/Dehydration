package net.dehydration.block;

import net.dehydration.block.entity.RainwaterCollectorBehavior;
import net.dehydration.init.BlockInit;
import net.dehydration.init.FluidInit;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.event.GameEvent;

public class RainwaterCollectorBlock extends AbstractRainwaterCollectorBlock {

    public RainwaterCollectorBlock(AbstractBlock.Settings settings) {
        super(settings, RainwaterCollectorBehavior.EMPTY_RAINWATER_COLLECTOR_BEHAVIOR);
    }

    @Override
    public boolean isFull(BlockState state) {
        return false;
    }

    public static boolean canFillWithPrecipitation(World world, Biome.Precipitation precipitation) {
        if (precipitation == Biome.Precipitation.RAIN) {
            return world.getRandom().nextFloat() < 0.6F;
        } else if (precipitation == Biome.Precipitation.SNOW) {
            return world.getRandom().nextFloat() < 0.15F;
        } else {
            return false;
        }
    }

    @Override
    public void precipitationTick(BlockState state, World world, BlockPos pos, Biome.Precipitation precipitation) {
        if (canFillWithPrecipitation(world, precipitation)) {
            if (precipitation == Biome.Precipitation.RAIN) {
                world.setBlockState(pos, BlockInit.RAINWATER_PURIFIED_WATER_COLLECTOR_BLOCK.getDefaultState());
                world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
            } else if (precipitation == Biome.Precipitation.SNOW) {
                world.setBlockState(pos, BlockInit.RAINWATER_POWDERED_COLLECTOR_BLOCK.getDefaultState());
                world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
            }

        }
    }

    @Override
    public boolean canBeFilledByDripstone(Fluid fluid) {
        return fluid == Fluids.WATER || fluid == FluidInit.PURIFIED_WATER;
    }

    @Override
    protected void fillFromDripstone(BlockState state, World world, BlockPos pos, Fluid fluid) {
        if (fluid == Fluids.WATER || fluid == FluidInit.PURIFIED_WATER) {
            world.setBlockState(pos, BlockInit.RAINWATER_PURIFIED_WATER_COLLECTOR_BLOCK.getDefaultState());
            world.syncWorldEvent(WorldEvents.POINTED_DRIPSTONE_DRIPS_WATER_INTO_CAULDRON, pos, 0);
            world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
        }

    }
}
