package net.dehydration.block;

import net.dehydration.block.entity.CampfireCauldronEntity;
import net.dehydration.init.BlockInit;
import net.dehydration.init.ItemInit;
import net.dehydration.init.SoundInit;
import net.dehydration.item.LeatherFlask;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
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
        this.setDefaultState((BlockState) ((BlockState) this.stateManager.getDefaultState()).with(LEVEL, 0));
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
        return (BlockState) this.getDefaultState().with(FACING, itemPlacementContext.getHorizontalPlayerFacing().rotateYClockwise());
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
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        if (world.getBlockState(pos.down()).isIn(BlockTags.CAMPFIRES)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        CampfireCauldronEntity campfireCauldronEntity = (CampfireCauldronEntity) world.getBlockEntity(pos);
        if (itemStack.isEmpty()) {
            return ActionResult.PASS;
        } else {
            int i = (Integer) state.get(LEVEL);
            Item item = itemStack.getItem();
            if (item == Items.WATER_BUCKET) {
                if (i < 4 && !world.isClient()) {
                    if (!player.isCreative()) {
                        player.setStackInHand(hand, new ItemStack(Items.BUCKET));
                    }
                    campfireCauldronEntity.onFillingCauldron();
                    this.setLevel(world, pos, state, 4);
                    world.playSound((PlayerEntity) null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
                return ActionResult.success(world.isClient());

            } else if (item == Items.BUCKET) {
                if (i == 4 && !world.isClient()) {
                    if (!player.isCreative()) {
                        itemStack.decrement(1);
                        if (itemStack.isEmpty()) {
                            player.setStackInHand(hand, new ItemStack(Items.WATER_BUCKET));
                        } else if (!player.getInventory().insertStack(new ItemStack(Items.WATER_BUCKET))) {
                            player.dropItem(new ItemStack(Items.WATER_BUCKET), false);
                        }
                    }
                    this.setLevel(world, pos, state, 0);
                    world.playSound((PlayerEntity) null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
                return ActionResult.success(world.isClient());

            } else {

                ItemStack newItemStack;
                if (item == Items.GLASS_BOTTLE) {
                    if (i > 0 && !world.isClient()) {
                        if (!player.isCreative()) {
                            itemStack.decrement(1);
                            if (this.isPurifiedWater(world, pos)) {
                                newItemStack = PotionUtil.setPotion(new ItemStack(Items.POTION), ItemInit.PURIFIED_WATER);
                            } else {
                                newItemStack = PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER);
                            }
                            if (player.getStackInHand(hand).isEmpty()) {
                                player.setStackInHand(hand, newItemStack);
                            } else if (!player.getInventory().insertStack(newItemStack)) {
                                player.dropItem(newItemStack, false);
                            }
                        }
                        world.playSound((PlayerEntity) null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        this.setLevel(world, pos, state, i - 1);
                    }
                    return ActionResult.success(world.isClient());

                } else if (item == Items.POTION && (PotionUtil.getPotion(itemStack) == Potions.WATER || PotionUtil.getPotion(itemStack) == ItemInit.PURIFIED_WATER)) {
                    if (i < 4 && !world.isClient()) {
                        if (!player.isCreative()) {
                            newItemStack = new ItemStack(Items.GLASS_BOTTLE);
                            player.setStackInHand(hand, newItemStack);
                        }
                        world.playSound((PlayerEntity) null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        if (PotionUtil.getPotion(itemStack) == Potions.WATER) {
                            campfireCauldronEntity.onFillingCauldron();
                        }
                        this.setLevel(world, pos, state, i + 1);
                    }
                    return ActionResult.success(world.isClient());

                } else {
                    if (i > 0 && item instanceof LeatherFlask) {
                        NbtCompound tags = itemStack.getNbt();
                        if (tags != null && tags.getInt("leather_flask") < 2 + ((LeatherFlask) item).addition) {
                            if (this.isPurifiedWater(world, pos)) {
                                if ((tags.getInt("purified_water") == 0 || tags.getInt("leather_flask") == 0)) {
                                    tags.putInt("purified_water", 0);
                                } else {
                                    tags.putInt("purified_water", 1);
                                }
                            } else {
                                tags.putInt("purified_water", 2);
                            }
                            tags.putInt("leather_flask", tags.getInt("leather_flask") + 1);
                            this.setLevel(world, pos, state, i - 1);
                            world.playSound((PlayerEntity) null, pos, SoundInit.FILL_FLASK_EVENT, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                            return ActionResult.success(world.isClient());
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
    public void precipitationTick(BlockState state, World world, BlockPos pos, Biome.Precipitation precipitation) {
        if (world.getRandom().nextFloat() < 0.2f && world.isSkyVisible(pos) && world.getBiome(pos).value().getTemperature() >= 0.15F && world.getBiome(pos).value().getTemperature() < 2F) {
            if (precipitation == Biome.Precipitation.RAIN && state.get(LEVEL) < 4) {
                this.setLevel(world, pos, state, state.get(LEVEL) + 1);
                world.emitGameEvent((Entity) null, GameEvent.FLUID_PLACE, pos);
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

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (random.nextInt(12) == 0 && this.isFireBurning(world, pos) && (Integer) state.get(LEVEL) > 0) {
            world.playSound((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, SoundInit.CAULDRON_BUBBLE_EVENT, SoundCategory.BLOCKS, 0.5F,
                    random.nextFloat() * 0.4F + 0.8F, false);
        }
    }

    public boolean isFull(BlockState state) {
        return (Integer) state.get(LEVEL) == 4;
    }

    public boolean isFireBurning(World world, BlockPos pos) {
        if (world.getBlockState(pos.down()).getBlock() instanceof CampfireBlock && CampfireBlock.isLitCampfire(world.getBlockState(pos.down()))) {
            return true;
        } else
            return false;
    }

    public boolean isPurifiedWater(World world, BlockPos pos) {
        if (((CampfireCauldronEntity) world.getBlockEntity(pos) != null)) {
            return ((CampfireCauldronEntity) world.getBlockEntity(pos)).isBoiled;
        } else
            return false;

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
