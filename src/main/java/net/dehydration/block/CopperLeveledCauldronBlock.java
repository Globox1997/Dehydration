package net.dehydration.block;

import net.dehydration.block.entity.CopperCauldronBehavior;
import net.dehydration.init.BlockInit;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import java.util.Map;
import java.util.function.Predicate;

public class CopperLeveledCauldronBlock extends AbstractCopperCauldronBlock {
    public static final IntegerProperty LEVEL;
    public static final Predicate<Biome.Precipitation> RAIN_PREDICATE;
    public static final Predicate<Biome.Precipitation> SNOW_PREDICATE;
    private final Predicate<Biome.Precipitation> precipitationPredicate;

    public CopperLeveledCauldronBlock(BlockBehaviour.Properties settings, Predicate<Biome.Precipitation> precipitationPredicate, Map<Item, CopperCauldronBehavior> behaviorMap) {
        super(settings, behaviorMap);
        this.precipitationPredicate = precipitationPredicate;
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 1));
    }

    @Override
    public boolean isFull(BlockState state) {
        return state.getValue(LEVEL) == 3;
    }

    @Override
    public boolean canBeFilledByDripstone(Fluid fluid) {
        return fluid == Fluids.WATER && this.precipitationPredicate == RAIN_PREDICATE;
    }

    @Override
    protected double getFluidHeight(BlockState state) {
        return (6.0D + (double) state.getValue(LEVEL) * 3.0D) / 16.0D;
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        if (!world.isClientSide() && entity.isOnFire() && this.isEntityTouchingFluid(state, pos, entity)) {
            entity.clearFire();
            if (entity.mayInteract(world, pos)) {
                decrementFluidLevel(state, world, pos);
            }
        }

    }

    @Override
    public void handlePrecipitation(BlockState state, Level world, BlockPos pos, Biome.Precipitation precipitation) {
        if (CopperCauldronBlock.canFillWithPrecipitation(world, precipitation) && state.getValue(LEVEL) != 3 && this.precipitationPredicate.test(precipitation)) {
            world.setBlockAndUpdate(pos, state.cycle(LEVEL));
        }
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        return state.getValue(LEVEL);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }

    @Override
    protected void fillFromDripstone(BlockState state, Level world, BlockPos pos, Fluid fluid) {
        if (!this.isFull(state)) {
            world.setBlockAndUpdate(pos, state.setValue(LEVEL, state.getValue(LEVEL) + 1));
            world.levelEvent(LevelEvent.SOUND_DRIP_WATER_INTO_CAULDRON, pos, 0);
        }
    }

    public static void decrementFluidLevel(BlockState state, Level world, BlockPos pos) {
        setFluidLevel(state, world, pos, state.getValue(LEVEL) - 1);
    }

    public static void setFluidLevel(BlockState state, Level world, BlockPos pos, int level) {
        world.setBlockAndUpdate(pos, level <= 0 ? BlockInit.COPPER_CAULDRON_BLOCK.defaultBlockState() : state.setValue(LEVEL, level));
    }

    static {
        LEVEL = BlockStateProperties.LEVEL_CAULDRON;
        RAIN_PREDICATE = (precipitation) -> precipitation == Biome.Precipitation.RAIN;
        SNOW_PREDICATE = (precipitation) -> precipitation == Biome.Precipitation.SNOW;
    }
}
