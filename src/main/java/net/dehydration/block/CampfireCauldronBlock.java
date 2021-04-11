package net.dehydration.block;

import net.dehydration.init.ItemInit;
import net.dehydration.init.SoundInit;
import net.dehydration.item.Leather_Flask;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class CampfireCauldronBlock extends Block {
    public static final DirectionProperty FACING;
    public static final IntProperty LEVEL;
    private static final VoxelShape Z_BASE_SHAPE;
    private static final VoxelShape X_BASE_SHAPE;
    private static final VoxelShape CAULDRON_SHAPE;

    public CampfireCauldronBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState) ((BlockState) this.stateManager.getDefaultState()).with(LEVEL, 0));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        return (BlockState) this.getDefaultState().with(FACING,
                itemPlacementContext.getPlayerFacing().rotateYClockwise());
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState) state.with(FACING, rotation.rotate((Direction) state.get(FACING)));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction direction = (Direction) state.get(FACING);
        return direction.getAxis() == Direction.Axis.X ? X_BASE_SHAPE : Z_BASE_SHAPE;
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.DESTROY;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        if (world.getBlockState(pos.down()).getBlock().isIn(BlockTags.CAMPFIRES)) {
            return true;
        } else
            return false;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isEmpty()) {
            return ActionResult.PASS;
        } else {
            int i = (Integer) state.get(LEVEL);
            Item item = itemStack.getItem();
            if (item == Items.WATER_BUCKET) {
                if (i < 4 && !world.isClient) {
                    if (!player.abilities.creativeMode) {
                        player.setStackInHand(hand, new ItemStack(Items.BUCKET));
                    }
                    this.setLevel(world, pos, state, 4);
                    world.playSound((PlayerEntity) null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F,
                            1.0F);
                }
                return ActionResult.success(world.isClient);

            } else if (item == Items.BUCKET) {
                if (i == 4 && !world.isClient) {
                    if (!player.abilities.creativeMode) {
                        itemStack.decrement(1);
                        if (itemStack.isEmpty()) {
                            player.setStackInHand(hand, new ItemStack(Items.WATER_BUCKET));
                        } else if (!player.inventory.insertStack(new ItemStack(Items.WATER_BUCKET))) {
                            player.dropItem(new ItemStack(Items.WATER_BUCKET), false);
                        }
                    }
                    this.setLevel(world, pos, state, 0);
                    world.playSound((PlayerEntity) null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F,
                            1.0F);
                }
                return ActionResult.success(world.isClient);

            } else {

                ItemStack newItemStack;
                if (item == Items.GLASS_BOTTLE) {
                    if (i > 0 && !world.isClient) {
                        if (!player.abilities.creativeMode) {
                            itemStack.decrement(1);
                            if (this.isFireBurning(world, pos)) {
                                newItemStack = PotionUtil.setPotion(new ItemStack(Items.POTION),
                                        ItemInit.PURIFIED_WATER);
                            } else {
                                newItemStack = PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER);
                            }
                            if (itemStack.isEmpty()) {
                                player.setStackInHand(hand, newItemStack);
                            } else if (!player.inventory.insertStack(newItemStack)) {
                                player.dropItem(newItemStack, false);
                            } else if (player instanceof ServerPlayerEntity) {
                                ((ServerPlayerEntity) player).refreshScreenHandler(player.playerScreenHandler);
                            }
                        }
                        world.playSound((PlayerEntity) null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS,
                                1.0F, 1.0F);
                        this.setLevel(world, pos, state, i - 1);
                    }
                    return ActionResult.success(world.isClient);

                } else if (item == Items.POTION && (PotionUtil.getPotion(itemStack) == Potions.WATER
                        || PotionUtil.getPotion(itemStack) == ItemInit.PURIFIED_WATER)) {
                    if (i < 4 && !world.isClient) {
                        if (!player.abilities.creativeMode) {
                            newItemStack = new ItemStack(Items.GLASS_BOTTLE);
                            player.setStackInHand(hand, newItemStack);
                            if (player instanceof ServerPlayerEntity) {
                                ((ServerPlayerEntity) player).refreshScreenHandler(player.playerScreenHandler);
                            }
                        }
                        world.playSound((PlayerEntity) null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS,
                                1.0F, 1.0F);
                        this.setLevel(world, pos, state, i + 1);
                    }
                    return ActionResult.success(world.isClient);

                } else {
                    if (i > 0 && item instanceof Leather_Flask) {
                        CompoundTag tags = itemStack.getTag();
                        if (tags != null && tags.getInt("leather_flask") < 2 + ((Leather_Flask) item).addition) {
                            if (this.isFireBurning(world, pos)) {
                                tags.putBoolean("purified_water", true);
                            } else {
                                tags.putBoolean("purified_water", false);
                            }
                            tags.putInt("leather_flask", tags.getInt("leather_flask") + 1);
                            this.setLevel(world, pos, state, i - 1);
                            world.playSound((PlayerEntity) null, pos, SoundInit.FILL_FLASK_EVENT, SoundCategory.NEUTRAL,
                                    1.0F, 1.0F);
                            return ActionResult.success(world.isClient);
                        } else
                            return ActionResult.PASS;

                    } else {
                        return ActionResult.PASS;
                    }
                }
            }
        }
    }

    public void setLevel(World world, BlockPos pos, BlockState state, int level) {
        world.setBlockState(pos, (BlockState) state.with(LEVEL, MathHelper.clamp(level, 0, 4)), 2);
        world.updateComparators(pos, this);
    }

    @Override
    public void rainTick(World world, BlockPos pos) {
        if (world.random.nextInt(20) == 1) {
            float f = world.getBiome(pos).getTemperature(pos);
            if (f >= 0.15F) {
                BlockState blockState = world.getBlockState(pos);
                if ((Integer) blockState.get(LEVEL) < 4) {
                    world.setBlockState(pos, (BlockState) blockState.cycle(LEVEL), 2);
                }

            }
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return (Integer) state.get(LEVEL);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
        builder.add(FACING);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    public boolean isFireBurning(World world, BlockPos pos) {
        if (world.getBlockState(pos.down()).getBlock() instanceof CampfireBlock
                && CampfireBlock.isLitCampfire(world.getBlockState(pos.down()))) {
            return true;
        } else
            return false;
    }

    static {
        LEVEL = IntProperty.of("level", 0, 4);
        FACING = HorizontalFacingBlock.FACING;
        CAULDRON_SHAPE = VoxelShapes.union(createCuboidShape(11, 1, 4, 12, 6, 5), createCuboidShape(4, 0, 4, 12, 1, 12),
                createCuboidShape(3, 1, 4, 4, 6, 12), createCuboidShape(12, 1, 4, 13, 6, 12),
                createCuboidShape(4, 1, 12, 12, 6, 13), createCuboidShape(4, 1, 3, 12, 6, 4),
                createCuboidShape(4, 1, 4, 5, 6, 5), createCuboidShape(11, 1, 11, 12, 6, 12),
                Block.createCuboidShape(4, 1, 11, 5, 6, 12));

        Z_BASE_SHAPE = VoxelShapes.union(CAULDRON_SHAPE, createCuboidShape(7, -15, 0, 9, 14, 1),
                createCuboidShape(7, 14, -1, 9, 16, 1), createCuboidShape(7, 14, 15, 9, 16, 17),
                createCuboidShape(7, -15, 15, 9, 14, 16), Block.createCuboidShape(7, 14, 1, 9, 15, 15));
        X_BASE_SHAPE = VoxelShapes.union(CAULDRON_SHAPE, createCuboidShape(15, -15, 7, 16, 14, 9),
                createCuboidShape(15, 14, 7, 17, 16, 9), createCuboidShape(-1, 14, 7, 1, 16, 9),
                createCuboidShape(0, -15, 7, 1, 14, 9), Block.createCuboidShape(1, 14, 7, 15, 15, 9));
    }

    // VoxelShapes.combineAndSimplify(Block.makeCuboidShape(6, 3, 13, 10, 4, 14),
    // Block.makeCuboidShape(6, 3, 2, 10, 4, 3), IBooleanFunction.OR)
    // VoxelShapes.combineAndSimplify(Block.makeCuboidShape(2, 3, 6, 3, 4, 10),
    // Block.makeCuboidShape(13, 3, 6, 14, 4, 10), IBooleanFunction.OR)

}
