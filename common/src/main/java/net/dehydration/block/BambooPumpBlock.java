package net.dehydration.block;

import net.dehydration.platform.Services;
import net.minecraft.world.InteractionResult;
import net.dehydration.block.entity.BambooPumpEntity;
import net.dehydration.init.BlockInit;
import net.dehydration.init.ConfigInit;
import net.dehydration.init.ItemInit;
import net.dehydration.item.LeatherFlask;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("deprecation")
public class BambooPumpBlock extends BaseEntityBlock {

    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty EXTENDED = BlockStateProperties.EXTENDED;
    public static final BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;
    private static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);

    public BambooPumpBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(ATTACHED, false).setValue(EXTENDED, false).setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BambooPumpEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BambooPumpEntity bambooPumpEntity = (BambooPumpEntity) world.getBlockEntity(pos);
        if (bambooPumpEntity != null) {
            ItemStack itemStack = bambooPumpEntity.getItem(0);
            ItemStack itemStack2 = player.getItemInHand(hand);
            if (itemStack.isEmpty()) {
                if (Services.fluid().canInsertFluid(itemStack2) || (itemStack2.getItem() instanceof LeatherFlask && !LeatherFlask.isFlaskFull(itemStack2))) {
                    if (!world.isClientSide()) {
                        if (player.isCreative()) {
                            bambooPumpEntity.setItem(0, itemStack2.copy());
                        } else {
                            bambooPumpEntity.setItem(0, itemStack2.split(1));
                        }
                        if (bambooPumpEntity.getItem(0).is(Items.BUCKET)) {
                            world.setBlock(pos, state.setValue(ATTACHED, true), Block.UPDATE_CLIENTS);
                        }
                    }
                    return InteractionResult.SUCCESS;
                }
                // can get used to place a water source block infront of the pump
                // else {
                // if (world.isClient()) {
                // if (state.get(EXTENDED))
                // world.playSound(player, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
                // } else
                // world.setBlockState(pos, state.with(EXTENDED, !state.get(EXTENDED)), Block.NOTIFY_LISTENERS);
                // }
                // return ActionResult.success(world.isClient());
            } else {
                if (itemStack2.isEmpty() && player.isShiftKeyDown()) {
                    if (!world.isClientSide()) {
                        player.setItemInHand(hand, bambooPumpEntity.getItem(0));
                        bambooPumpEntity.clearContent();
                        world.setBlock(pos, state.setValue(ATTACHED, false).setValue(EXTENDED, false), Block.UPDATE_CLIENTS);
                    }
                    return InteractionResult.SUCCESS;
                }
                if (Services.fluid().canInsertFluid(itemStack) || (itemStack.getItem() instanceof LeatherFlask && !LeatherFlask.isFlaskFull(itemStack))) {
                    if (ConfigInit.CONFIG.pump_requires_water) {
                        boolean foundWater = false;
                        for (int i = 0; i < 50; i++) {
                            BlockPos checkPos = pos.below(10 + i);
                            if (world.getBlockState(checkPos).isAir()) {
                                break;
                            }
                            if (world.getBlockState(checkPos).getFluidState().is(FluidTags.WATER)) {
                                boolean notEnoughWater = false;
                                for (Direction direction : Direction.values()) {
                                    if (!world.getBlockState(checkPos.relative(direction)).getFluidState().is(FluidTags.WATER)) {
                                        notEnoughWater = true;
                                        break;
                                    }
                                }
                                if (!notEnoughWater) {
                                    foundWater = true;
                                    break;
                                }
                            }
                        }
                        if (!foundWater) {
                            if (world.isClientSide()) {
                                if (state.getValue(EXTENDED)) {
                                    world.playSound(player, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
                                }
                            } else {
                                player.sendOverlayMessage(Component.translatable("block.dehydration.bamboo_pump.no_water"));
                            }
                            return InteractionResult.FAIL;
                        }
                    }
                    if (ConfigInit.CONFIG.pump_cooldown != 0 && bambooPumpEntity.getCooldown() > 0) {
                        if (world.isClientSide()) {
                            if (state.getValue(EXTENDED)) {
                                world.playSound(player, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
                            }
                        } else {
                            player.sendOverlayMessage(Component.translatable("block.dehydration.bamboo_pump.cooldown", bambooPumpEntity.getCooldown() / 20));
                        }
                        return InteractionResult.FAIL;
                    }

                    if (state.getValue(EXTENDED)) {
                        bambooPumpEntity.increasePumpCount(1);
                    }
                    if (world.isClientSide()) {
                        if (state.getValue(EXTENDED)) {
                            world.playSound(player, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
                        }
                    } else {
                        world.setBlock(pos, state.setValue(EXTENDED, !state.getValue(EXTENDED)), Block.UPDATE_CLIENTS);
                    }
                    return InteractionResult.SUCCESS;
                }
            }

        }

        return InteractionResult.FAIL;

    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return world.getBlockState(pos.below()).isSolid();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ATTACHED, EXTENDED, FACING, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.getValue(WATERLOGGED).booleanValue())
            return Fluids.WATER.getSource(false);

        return super.getFluidState(state);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (direction == Direction.DOWN && !state.canSurvive(world, pos)) {
            return Blocks.AIR.defaultBlockState();
        } else if (state.getValue(WATERLOGGED)) {
            tickView.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        return super.updateShape(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER).setValue(EXTENDED, false);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean moved) {
        Containers.updateNeighboursAfterDestroy(state, world, pos);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, Builder builder) {
        List<ItemStack> stacks = super.getDrops(state, builder);
        for (ItemStack itemStack : stacks) {
            if (itemStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock().equals(BlockInit.BAMBOO_PUMP_BLOCK)
                    && builder.getParameter(LootContextParams.BLOCK_ENTITY) instanceof BambooPumpEntity bambooPumpEntity) {
                itemStack.set(ItemInit.COOLDOWN, bambooPumpEntity.getCooldown());
                break;
            }
        }
        return stacks;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(world, pos, state, placer, itemStack);
        if (itemStack.get(ItemInit.COOLDOWN) != null && world.getBlockEntity(pos) instanceof BambooPumpEntity bambooPumpEntity) {
            bambooPumpEntity.setCooldown(itemStack.get(ItemInit.COOLDOWN));
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, BlockInit.BAMBOO_PUMP_ENTITY, world.isClientSide() ? BambooPumpEntity::clientTick : BambooPumpEntity::serverTick);
    }

    @Override
    public void attack(BlockState state, Level world, BlockPos pos, Player player) {
        if (world.isClientSide())
            return;
        BambooPumpEntity bambooPumpEntity = (BambooPumpEntity) world.getBlockEntity(pos);
        if (bambooPumpEntity == null)
            return;
        ItemStack itemStack = bambooPumpEntity.getItem(0);
        if (itemStack.isEmpty())
            return;
        if (player.addItem(itemStack)) {
            bambooPumpEntity.clearContent();
            world.setBlock(pos, state.setValue(ATTACHED, false).setValue(EXTENDED, false), Block.UPDATE_CLIENTS);
        }
    }

}
