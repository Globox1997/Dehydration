package net.dehydration.item;

import org.jetbrains.annotations.Nullable;

import net.dehydration.init.FluidInit;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidFillable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.FluidModificationItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;

public class PurifiedBucket extends Item implements FluidModificationItem {

    public PurifiedBucket(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        BlockHitResult blockHitResult = BucketItem.raycast(world, user, RaycastContext.FluidHandling.NONE);
        if (blockHitResult.getType() == HitResult.Type.MISS) {
            return TypedActionResult.pass(itemStack);
        }
        if (blockHitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos3;
            BlockPos blockPos = blockHitResult.getBlockPos();
            Direction direction = blockHitResult.getSide();
            BlockPos blockPos2 = blockPos.offset(direction);
            if (!world.canPlayerModifyAt(user, blockPos) || !user.canPlaceOn(blockPos2, direction, itemStack)) {
                return TypedActionResult.fail(itemStack);
            }

            BlockState blockState = world.getBlockState(blockPos);
            blockPos3 = blockState.getBlock() instanceof FluidFillable ? blockPos : blockPos2;
            if (this.placeFluid(user, world, blockPos3, blockHitResult)) {
                this.onEmptied(user, world, itemStack, blockPos3);
                if (user instanceof ServerPlayerEntity) {
                    Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity) user, blockPos3, itemStack);
                }
                user.incrementStat(Stats.USED.getOrCreateStat(this));
                return TypedActionResult.success(BucketItem.getEmptiedStack(itemStack, user), world.isClient());
            }
            return TypedActionResult.fail(itemStack);
        }
        return TypedActionResult.pass(itemStack);
    }

    @Override
    public void onEmptied(PlayerEntity player, World world, ItemStack stack, BlockPos pos) {
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean placeFluid(@Nullable PlayerEntity player, World world, BlockPos pos, @Nullable BlockHitResult hitResult) {
        boolean bl2;

        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        boolean bl = blockState.canBucketPlace(FluidInit.PURIFIED_WATER);
        bl2 = blockState.isAir() || bl || block instanceof FluidFillable && ((FluidFillable) ((Object) block)).canFillWithFluid(world, pos, blockState, FluidInit.PURIFIED_WATER);
        if (!bl2) {
            return hitResult != null && this.placeFluid(player, world, hitResult.getBlockPos().offset(hitResult.getSide()), null);
        }
        if (world.getDimension().ultrawarm()) {
            int i = pos.getX();
            int j = pos.getY();
            int k = pos.getZ();
            world.playSound(player, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 2.6f + (world.random.nextFloat() - world.random.nextFloat()) * 0.8f);
            for (int l = 0; l < 8; ++l) {
                world.addParticle(ParticleTypes.LARGE_SMOKE, (double) i + Math.random(), (double) j + Math.random(), (double) k + Math.random(), 0.0, 0.0, 0.0);
            }
            return true;
        }
        if (block instanceof FluidFillable) {
            ((FluidFillable) ((Object) block)).tryFillWithFluid(world, pos, blockState, ((FlowableFluid) FluidInit.PURIFIED_WATER).getStill(false));

            this.playEmptyingSound(player, world, pos);
            return true;
        }
        if (!world.isClient() && bl && !blockState.isLiquid()) {
            world.breakBlock(pos, true);
        }
        if (world.setBlockState(pos, FluidInit.PURIFIED_WATER.getDefaultState().getBlockState(), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD) || blockState.getFluidState().isStill()) {
            this.playEmptyingSound(player, world, pos);
            return true;
        }
        return false;
    }

    private void playEmptyingSound(@Nullable PlayerEntity player, WorldAccess world, BlockPos pos) {
        world.playSound(player, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
        world.emitGameEvent((Entity) player, GameEvent.FLUID_PLACE, pos);
    }

}
