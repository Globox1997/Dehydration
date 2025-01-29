package net.dehydration.block;

import net.dehydration.block.entity.CampfireCauldronEntity;
import net.dehydration.init.BlockInit;
import net.dehydration.init.SoundInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorageUtil;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class CampfireCauldronBlock extends Block implements BlockEntityProvider {
    public static final DirectionProperty FACING;
    public static final IntProperty LEVEL;
    private static final VoxelShape Z_BASE_SHAPE;
    private static final VoxelShape X_BASE_SHAPE;
    private static final VoxelShape CAULDRON_SHAPE;

    public CampfireCauldronBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(LEVEL, 0));
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CampfireCauldronEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, BlockInit.CAMPFIRE_CAULDRON_ENTITY, world.isClient ? CampfireCauldronEntity::clientTick : CampfireCauldronEntity::serverTick);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        return this.getDefaultState().with(FACING, itemPlacementContext.getHorizontalPlayerFacing().rotateYClockwise());
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction direction = state.get(FACING);
        return direction.getAxis() == Direction.Axis.X ? X_BASE_SHAPE : Z_BASE_SHAPE;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return world.getBlockState(pos.down()).isIn(BlockTags.CAMPFIRES);
    }

    @Override
    public ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.getStackInHand(hand).isEmpty()) {
            Storage<FluidVariant> storage = FluidStorage.SIDED.find(world, pos, hit.getSide().getOpposite());
            if (storage != null && FluidStorageUtil.interactWithFluidStorage(storage, player, hand)) {
                return ItemActionResult.success(world.isClient());
            }
        }
        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public void setLevel(World world, BlockPos pos, BlockState state, int level) {
        world.setBlockState(pos, state.with(LEVEL, MathHelper.clamp(level, 0, 4)), 2);
        world.updateComparators(pos, this);
    }

    @Override
    public void precipitationTick(BlockState state, World world, BlockPos pos, Biome.Precipitation precipitation) {
        if (world.getRandom().nextFloat() < 0.2f && world.isSkyVisible(pos) && world.getBiome(pos).value().getTemperature() >= 0.15F && world.getBiome(pos).value().getTemperature() < 2F) {
            if (precipitation == Biome.Precipitation.RAIN && state.get(LEVEL) < 4) {
                this.setLevel(world, pos, state, state.get(LEVEL) + 1);
                world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
            }
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return state.get(LEVEL);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
        builder.add(FACING);
    }

    @Override
    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (random.nextInt(12) == 0 && this.isFireBurning(world, pos) && state.get(LEVEL) > 0) {
            world.playSound((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, SoundInit.CAULDRON_BUBBLE_EVENT, SoundCategory.BLOCKS, 0.5F,
                    random.nextFloat() * 0.4F + 0.8F, false);
        }
    }

    public boolean isFull(BlockState state) {
        return state.get(LEVEL) == 4;
    }

    public boolean isFireBurning(World world, BlockPos pos) {
        return world.getBlockState(pos.down()).getBlock() instanceof CampfireBlock && CampfireBlock.isLitCampfire(world.getBlockState(pos.down()));
    }

    public boolean isPurifiedWater(World world, BlockPos pos) {
        if (world.getBlockEntity(pos) != null) {
            return ((CampfireCauldronEntity) world.getBlockEntity(pos)).isBoiled;
        } else {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType,
                                                                                                   BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }

    static {
        LEVEL = IntProperty.of("level", 0, 4);
        FACING = HorizontalFacingBlock.FACING;
        CAULDRON_SHAPE = VoxelShapes.union(createCuboidShape(11, 1, 4, 12, 6, 5), createCuboidShape(4, 0, 4, 12, 1, 12), createCuboidShape(3, 1, 4, 4, 6, 12), createCuboidShape(12, 1, 4, 13, 6, 12),
                createCuboidShape(4, 1, 12, 12, 6, 13), createCuboidShape(4, 1, 3, 12, 6, 4), createCuboidShape(4, 1, 4, 5, 6, 5), createCuboidShape(11, 1, 11, 12, 6, 12),
                Block.createCuboidShape(4, 1, 11, 5, 6, 12));
        Z_BASE_SHAPE = VoxelShapes.union(CAULDRON_SHAPE, createCuboidShape(7, -15, 0, 9, 14, 1), createCuboidShape(7, 14, -1, 9, 16, 1), createCuboidShape(7, 14, 15, 9, 16, 17),
                createCuboidShape(7, -15, 15, 9, 14, 16), Block.createCuboidShape(7, 14, 1, 9, 15, 15));
        X_BASE_SHAPE = VoxelShapes.union(CAULDRON_SHAPE, createCuboidShape(15, -15, 7, 16, 14, 9), createCuboidShape(15, 14, 7, 17, 16, 9), createCuboidShape(-1, 14, 7, 1, 16, 9),
                createCuboidShape(0, -15, 7, 1, 14, 9), Block.createCuboidShape(1, 14, 7, 15, 15, 9));
    }

}
