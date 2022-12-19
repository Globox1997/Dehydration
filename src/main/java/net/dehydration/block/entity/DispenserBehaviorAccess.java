package net.dehydration.block.entity;

import net.dehydration.block.CampfireCauldronBlock;
import net.dehydration.block.CopperLeveledCauldronBlock;
import net.dehydration.init.BlockInit;
import net.dehydration.init.ItemInit;
import net.dehydration.item.LeatherFlask;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class DispenserBehaviorAccess {

    public static void registerDefaults() {

        ItemDispenserBehavior itemDispenserBehavior = new FallibleItemDispenserBehavior() {
            @Override
            public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                this.setSuccess(false);
                ServerWorld serverWorld = pointer.getWorld();
                BlockPos blockPos = pointer.getPos().offset((Direction) pointer.getBlockState().get(DispenserBlock.FACING));
                BlockState blockState = serverWorld.getBlockState(blockPos);
                if (blockState.isOf(BlockInit.CAMPFIRE_CAULDRON_BLOCK) && blockState.get(CampfireCauldronBlock.LEVEL) > 0) {
                    CampfireCauldronBlock campfireCauldronBlock = (CampfireCauldronBlock) blockState.getBlock();
                    if (campfireCauldronBlock.isPurifiedWater(serverWorld, blockPos) && stack.hasNbt() && stack.getNbt().getInt("leather_flask") < 2 + ((LeatherFlask) stack.getItem()).addition) {
                        this.setSuccess(true);
                        campfireCauldronBlock.setLevel(serverWorld, blockPos, blockState, blockState.get(CampfireCauldronBlock.LEVEL) - 1);
                        return getNewFlask(stack, pointer);
                    }
                } else if (blockState.isOf(BlockInit.COPPER_PURIFIED_WATER_CAULDRON_BLOCK) && blockState.get(CopperLeveledCauldronBlock.LEVEL) > 0 && stack.hasNbt()
                        && stack.getNbt().getInt("leather_flask") < 2 + ((LeatherFlask) stack.getItem()).addition) {
                    this.setSuccess(true);
                    CopperLeveledCauldronBlock.decrementFluidLevel(blockState, serverWorld, blockPos);

                    return getNewFlask(stack, pointer);
                }
                return super.dispenseSilently(pointer, stack);
            }
        };
        for (int i = 0; i < ItemInit.FLASK_ITEM_LIST.size(); i++) {
            DispenserBlock.registerBehavior(ItemInit.FLASK_ITEM_LIST.get(i), itemDispenserBehavior);
        }
    }

    private static ItemStack getNewFlask(ItemStack stack, BlockPointer pointer) {
        ItemStack newStack = stack.copy();
        NbtCompound tags = new NbtCompound();
        tags.putInt("leather_flask", 2 + ((LeatherFlask) newStack.getItem()).addition);
        int waterPurity = 0;
        if (stack.getNbt().getInt("leather_flask") != 0 && newStack.getNbt().getInt("purified_water") != 0) {
            waterPurity = 1;
        }
        tags.putInt("purified_water", waterPurity);
        newStack.setNbt(tags);
        stack.decrement(1);

        if (stack.isEmpty()) {
            return newStack.copy();
        } else {
            if (((DispenserBlockEntity) pointer.getBlockEntity()).addToFirstFreeSlot(newStack.copy()) < 0) {
                new ItemDispenserBehavior().dispense(pointer, newStack.copy());
            }

            return stack;
        }
    }
}