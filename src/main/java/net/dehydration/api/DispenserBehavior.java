package net.dehydration.api;

import net.dehydration.block.CampfireCauldronBlock;
import net.dehydration.block.CopperLeveledCauldronBlock;
import net.dehydration.init.BlockInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;

public class DispenserBehavior {

    public static void registerPurifiedBucket(Item filledBucket, Item emptyBucket) {
        DispenserBlock.registerBehavior(filledBucket, new OptionalDispenseItemBehavior() {
            @Override
            public ItemStack execute(BlockSource pointer, ItemStack stack) {
                this.setSuccess(false);
                ServerLevel world = pointer.level();
                BlockPos pos = pointer.pos().relative(pointer.state().getValue(DispenserBlock.FACING));
                BlockState blockState = world.getBlockState(pos);

                if (blockState.is(BlockInit.CAMPFIRE_CAULDRON_BLOCK)) {
                    CampfireCauldronBlock cauldron = (CampfireCauldronBlock) blockState.getBlock();
                    int level = blockState.getValue(CampfireCauldronBlock.LEVEL);
                    if (level < 3) {
                        this.setSuccess(true);
                        cauldron.setLevel(world, pos, blockState, level + 1);
                        return new ItemStack(emptyBucket);
                    }
                }

                if (blockState.is(BlockInit.COPPER_PURIFIED_WATER_CAULDRON_BLOCK)) {
                    int level = blockState.getValue(CopperLeveledCauldronBlock.LEVEL);
                    if (level < 3) {
                        this.setSuccess(true);
                        CopperLeveledCauldronBlock.setFluidLevel(blockState, world, pos, level + 1);
                        return new ItemStack(emptyBucket);
                    }
                }

                return super.execute(pointer, stack);
            }
        });
    }
}