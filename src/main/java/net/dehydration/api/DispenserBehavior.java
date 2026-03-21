package net.dehydration.api;

import net.dehydration.block.CampfireCauldronBlock;
import net.dehydration.block.CopperLeveledCauldronBlock;
import net.dehydration.init.BlockInit;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;

public class DispenserBehavior {

    public static void registerPurifiedBucket(Item filledBucket, Item emptyBucket) {
        DispenserBlock.registerBehavior(filledBucket, new FallibleItemDispenserBehavior() {
            @Override
            public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                this.setSuccess(false);
                ServerWorld world = pointer.world();
                BlockPos pos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING));
                BlockState blockState = world.getBlockState(pos);

                if (blockState.isOf(BlockInit.CAMPFIRE_CAULDRON_BLOCK)) {
                    CampfireCauldronBlock cauldron = (CampfireCauldronBlock) blockState.getBlock();
                    int level = blockState.get(CampfireCauldronBlock.LEVEL);
                    if (level < 3) {
                        this.setSuccess(true);
                        cauldron.setLevel(world, pos, blockState, level + 1);
                        return new ItemStack(emptyBucket);
                    }
                }

                if (blockState.isOf(BlockInit.COPPER_PURIFIED_WATER_CAULDRON_BLOCK)) {
                    int level = blockState.get(CopperLeveledCauldronBlock.LEVEL);
                    if (level < 3) {
                        this.setSuccess(true);
                        CopperLeveledCauldronBlock.setFluidLevel(blockState, world, pos, level + 1);
                        return new ItemStack(emptyBucket);
                    }
                }

                return super.dispenseSilently(pointer, stack);
            }
        });
    }
}