package net.dehydration.block;

import net.dehydration.platform.Services;
import net.minecraft.world.InteractionResult;
import net.dehydration.block.entity.CampfireCauldronEntity;
import net.dehydration.init.BlockInit;
import net.dehydration.init.SoundInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class CampfireCauldronBlock extends Block implements EntityBlock {
    public static final EnumProperty<Direction> FACING;
    public static final IntegerProperty LEVEL;
    private static final VoxelShape Z_BASE_SHAPE;
    private static final VoxelShape X_BASE_SHAPE;
    private static final VoxelShape CAULDRON_SHAPE;

    public CampfireCauldronBlock(BlockBehaviour.Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 0));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CampfireCauldronEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return world.isClientSide() ? null : checkType(type, BlockInit.CAMPFIRE_CAULDRON_ENTITY, CampfireCauldronEntity::serverTick);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext itemPlacementContext) {
        return this.defaultBlockState().setValue(FACING, itemPlacementContext.getHorizontalDirection().getClockWise());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        return direction.getAxis() == Direction.Axis.X ? X_BASE_SHAPE : Z_BASE_SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return world.getBlockState(pos.below()).is(BlockTags.CAMPFIRES);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (direction == Direction.DOWN && !state.canSurvive(world, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!player.getItemInHand(hand).isEmpty()) {
            if (Services.fluid().interactWithFluidStorage(world, pos, hit.getDirection().getOpposite(), player, hand)) {
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    public void setLevel(Level world, BlockPos pos, BlockState state, int level) {
        world.setBlock(pos, state.setValue(LEVEL, Mth.clamp(level, 0, 4)), 2);
        world.updateNeighbourForOutputSignal(pos, this);
    }

    @Override
    public void handlePrecipitation(BlockState state, Level world, BlockPos pos, Biome.Precipitation precipitation) {
        if (CopperCauldronBlock.canFillWithPrecipitation(world, precipitation)) {
            if (state.getValue(LEVEL) < 4) {
                this.setLevel(world, pos, state, state.getValue(LEVEL) + 1);
                world.gameEvent(null, GameEvent.FLUID_PLACE, pos);
            }
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos, Direction direction) {
        return state.getValue(LEVEL);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
        builder.add(FACING);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        if (random.nextInt(12) == 0 && this.isFireBurning(world, pos) && state.getValue(LEVEL) > 0) {
            world.playLocalSound((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, SoundInit.CAULDRON_BUBBLE_EVENT, SoundSource.BLOCKS, 0.5F,
                    random.nextFloat() * 0.4F + 0.8F, false);
        }
    }

    public boolean isFull(BlockState state) {
        return state.getValue(LEVEL) == 4;
    }

    public boolean isFireBurning(Level world, BlockPos pos) {
        return world.getBlockState(pos.below()).getBlock() instanceof CampfireBlock && CampfireBlock.isLitCampfire(world.getBlockState(pos.below()));
    }

    public boolean isPurifiedWater(Level world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof CampfireCauldronEntity campfireCauldronEntity) {
            return campfireCauldronEntity.isBoiled;
        } else {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType,
                                                                                                   BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        BlockPos blockPos = PointedDripstoneBlock.findStalactiteTipAboveCauldron(world, pos);
        if (blockPos != null) {
            Fluid fluid = PointedDripstoneBlock.getCauldronFillFluidType(world, (BlockPos) blockPos);
            if (fluid != Fluids.EMPTY && this.canBeFilledByDripstone(fluid)) {
                this.fillFromDripstone(state, world, pos, fluid);
            }
        }
    }

    public boolean canBeFilledByDripstone(Fluid fluid) {
        return fluid == Fluids.WATER;
    }

    protected void fillFromDripstone(BlockState state, Level world, BlockPos pos, Fluid fluid) {
        if (!this.isFull(state)) {
            world.setBlockAndUpdate(pos, state.setValue(LEVEL, state.getValue(LEVEL) + 1));
            world.levelEvent(LevelEvent.SOUND_DRIP_WATER_INTO_CAULDRON, pos, 0);
        }
    }

    static {
        LEVEL = IntegerProperty.create("level", 0, 4);
        FACING = HorizontalDirectionalBlock.FACING;
        CAULDRON_SHAPE = Shapes.or(box(11, 1, 4, 12, 6, 5), box(4, 0, 4, 12, 1, 12), box(3, 1, 4, 4, 6, 12), box(12, 1, 4, 13, 6, 12),
                box(4, 1, 12, 12, 6, 13), box(4, 1, 3, 12, 6, 4), box(4, 1, 4, 5, 6, 5), box(11, 1, 11, 12, 6, 12),
                Block.box(4, 1, 11, 5, 6, 12));
        Z_BASE_SHAPE = Shapes.or(CAULDRON_SHAPE, box(7, -15, 0, 9, 14, 1), box(7, 14, -1, 9, 16, 1), box(7, 14, 15, 9, 16, 17),
                box(7, -15, 15, 9, 14, 16), Block.box(7, 14, 1, 9, 15, 15));
        X_BASE_SHAPE = Shapes.or(CAULDRON_SHAPE, box(15, -15, 7, 16, 14, 9), box(15, 14, 7, 17, 16, 9), box(-1, 14, 7, 1, 16, 9),
                box(0, -15, 7, 1, 14, 9), Block.box(1, 14, 7, 15, 15, 9));
    }

}
