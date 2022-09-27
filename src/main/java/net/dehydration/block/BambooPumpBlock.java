package net.dehydration.block;

import net.dehydration.block.entity.BambooPumpEntity;
import net.dehydration.item.Leather_Flask;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

@SuppressWarnings("deprecation")
public class BambooPumpBlock extends BlockWithEntity {

    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final BooleanProperty EXTENDED = Properties.EXTENDED;
    public static final BooleanProperty ATTACHED = Properties.ATTACHED;
    private static final VoxelShape SHAPE = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);

    public BambooPumpBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(ATTACHED, false).with(EXTENDED, false).with(FACING, Direction.NORTH).with(WATERLOGGED, false));
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BambooPumpEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BambooPumpEntity bambooPumpEntity = (BambooPumpEntity) world.getBlockEntity(pos);
        if (bambooPumpEntity != null) {
            ItemStack itemStack = bambooPumpEntity.getStack(0);
            ItemStack itemStack2 = player.getStackInHand(hand);
            if (itemStack.isEmpty()) {
                if (itemStack2.isOf(Items.BUCKET) || itemStack2.isOf(Items.GLASS_BOTTLE) || (itemStack2.getItem() instanceof Leather_Flask && !Leather_Flask.isFlaskFull(itemStack2))) {
                    if (!world.isClient) {
                        if (player.isCreative())
                            bambooPumpEntity.setStack(0, itemStack2.copy());
                        else
                            bambooPumpEntity.setStack(0, itemStack2.split(1));

                        if (bambooPumpEntity.getStack(0).isOf(Items.BUCKET))
                            world.setBlockState(pos, state.with(ATTACHED, true), Block.NOTIFY_LISTENERS);
                    }
                    return ActionResult.success(world.isClient);
                }
                // can get used to place a water source block infront of the pump
                // else {
                // if (world.isClient) {
                // if (state.get(EXTENDED))
                // world.playSound(player, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
                // } else
                // world.setBlockState(pos, state.with(EXTENDED, !state.get(EXTENDED)), Block.NOTIFY_LISTENERS);
                // }
                // return ActionResult.success(world.isClient);
            } else {
                if (itemStack2.isEmpty() && player.isSneaking()) {
                    if (!world.isClient) {
                        player.setStackInHand(hand, bambooPumpEntity.getStack(0));
                        bambooPumpEntity.clear();
                        world.setBlockState(pos, state.with(ATTACHED, false).with(EXTENDED, false), Block.NOTIFY_LISTENERS);
                    }
                    return ActionResult.success(world.isClient);
                }
                if (itemStack.isOf(Items.BUCKET) || itemStack.isOf(Items.GLASS_BOTTLE) || (itemStack.getItem() instanceof Leather_Flask && !Leather_Flask.isFlaskFull(itemStack))) {
                    if (world.isClient) {
                        if (state.get(EXTENDED))
                            world.playSound(player, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    } else {
                        world.setBlockState(pos, state.with(EXTENDED, !state.get(EXTENDED)), Block.NOTIFY_LISTENERS);
                        if (state.get(EXTENDED))
                            bambooPumpEntity.increasePumpCount(1);
                    }

                    return ActionResult.success(world.isClient);
                }
            }

        }

        return ActionResult.FAIL;

    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return world.getBlockState(pos.down()).getMaterial().isSolid();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ATTACHED, EXTENDED, FACING, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.get(WATERLOGGED).booleanValue())
            return Fluids.WATER.getStill(false);

        return super.getFluidState(state);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        } else if ((Boolean) state.get(WATERLOGGED)) {
            world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        return (BlockState) this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite()).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER).with(EXTENDED, false);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState) state.with(FACING, rotation.rotate((Direction) state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction) state.get(FACING)));
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof Inventory) {
                ItemScatterer.spawn(world, pos, (Inventory) blockEntity);
                world.updateComparators(pos, this);
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

}
