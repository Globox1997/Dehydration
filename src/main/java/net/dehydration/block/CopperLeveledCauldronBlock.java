package net.dehydration.block;

import java.util.Map;
import java.util.function.Predicate;

import net.dehydration.block.entity.CopperCauldronBehavior;
import net.dehydration.init.BlockInit;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.biome.Biome;

public class CopperLeveledCauldronBlock extends AbstractCopperCauldronBlock {
    public static final IntProperty LEVEL;
    public static final Predicate<Biome.Precipitation> RAIN_PREDICATE;
    public static final Predicate<Biome.Precipitation> SNOW_PREDICATE;
    private final Predicate<Biome.Precipitation> precipitationPredicate;

    public CopperLeveledCauldronBlock(AbstractBlock.Settings settings, Predicate<Biome.Precipitation> precipitationPredicate, Map<Item, CopperCauldronBehavior> behaviorMap) {
        super(settings, behaviorMap);
        this.precipitationPredicate = precipitationPredicate;
        this.setDefaultState((BlockState) ((BlockState) this.stateManager.getDefaultState()).with(LEVEL, 1));
    }

    @Override
    public boolean isFull(BlockState state) {
        return (Integer) state.get(LEVEL) == 3;
    }

    @Override
    protected boolean canBeFilledByDripstone(Fluid fluid) {
        return fluid == Fluids.WATER && this.precipitationPredicate == RAIN_PREDICATE;
    }

    @Override
    protected double getFluidHeight(BlockState state) {
        return (6.0D + (double) (Integer) state.get(LEVEL) * 3.0D) / 16.0D;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!world.isClient() && entity.isOnFire() && this.isEntityTouchingFluid(state, pos, entity)) {
            entity.extinguish();
            if (entity.canModifyAt(world, pos)) {
                this.onFireCollision(state, world, pos);
            }
        }

    }

    protected void onFireCollision(BlockState state, World world, BlockPos pos) {
        decrementFluidLevel(state, world, pos);
    }

    public static void decrementFluidLevel(BlockState state, World world, BlockPos pos) {
        int i = (Integer) state.get(LEVEL) - 1;
        world.setBlockState(pos, i <= 0 ? BlockInit.COPPER_CAULDRON_BLOCK.getDefaultState() : (BlockState) state.with(LEVEL, i));
    }

    @Override
    public void precipitationTick(BlockState state, World world, BlockPos pos, Biome.Precipitation precipitation) {
        if (CopperCauldronBlock.canFillWithPrecipitation(world, precipitation) && (Integer) state.get(LEVEL) != 3 && this.precipitationPredicate.test(precipitation)) {
            world.setBlockState(pos, (BlockState) state.cycle(LEVEL));
        }
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return (Integer) state.get(LEVEL);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }

    @Override
    protected void fillFromDripstone(BlockState state, World world, BlockPos pos, Fluid fluid) {
        if (!this.isFull(state)) {
            world.setBlockState(pos, (BlockState) state.with(LEVEL, (Integer) state.get(LEVEL) + 1));
            world.syncWorldEvent(WorldEvents.POINTED_DRIPSTONE_DRIPS_WATER_INTO_CAULDRON, pos, 0);
        }
    }

    static {
        LEVEL = Properties.LEVEL_3;
        RAIN_PREDICATE = (precipitation) -> {
            return precipitation == Biome.Precipitation.RAIN;
        };
        SNOW_PREDICATE = (precipitation) -> {
            return precipitation == Biome.Precipitation.SNOW;
        };
    }
}
