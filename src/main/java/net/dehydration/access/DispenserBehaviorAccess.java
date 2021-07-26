package net.dehydration.access;

import net.dehydration.block.CampfireCauldronBlock;
import net.dehydration.init.BlockInit;
import net.dehydration.init.ItemInit;
import net.dehydration.item.Leather_Flask;
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
                    if (campfireCauldronBlock.isPurifiedWater(serverWorld, blockPos) && stack.hasNbt() && stack.getNbt().getInt("leather_flask") < 2 + ((Leather_Flask) stack.getItem()).addition) {
                        this.setSuccess(true);
                        ItemStack newStack = stack.copy();
                        NbtCompound tags = new NbtCompound();
                        tags.putInt("leather_flask", 2 + ((Leather_Flask) newStack.getItem()).addition);
                        int waterPurity = 2;
                        if (stack.getNbt().getInt("leather_flask") != 0 && newStack.getNbt().getInt("purified_water") != 2) {
                            waterPurity = 1;
                        }
                        tags.putInt("purified_water", waterPurity);
                        newStack.setNbt(tags);
                        stack.decrement(1);
                        campfireCauldronBlock.setLevel(serverWorld, blockPos, blockState, blockState.get(CampfireCauldronBlock.LEVEL) - 1);
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
                return super.dispenseSilently(pointer, stack);
            }
        };
        DispenserBlock.registerBehavior(ItemInit.LEATHER_FLASK, itemDispenserBehavior);
        DispenserBlock.registerBehavior(ItemInit.IRON_LEATHER_FLASK, itemDispenserBehavior);
        DispenserBlock.registerBehavior(ItemInit.GOLDEN_LEATHER_FLASK, itemDispenserBehavior);
        DispenserBlock.registerBehavior(ItemInit.DIAMOND_LEATHER_FLASK, itemDispenserBehavior);
        DispenserBlock.registerBehavior(ItemInit.NETHERITE_LEATHER_FLASK, itemDispenserBehavior);
    }
}

// ItemStack potion = PotionUtil.setPotion(new ItemStack(Items.POTION), ItemInit.PURIFIED_WATER);
// if (stack.isEmpty()) {
// return potion.copy();
// } else {
// if (((DispenserBlockEntity) pointer.getBlockEntity()).addToFirstFreeSlot(potion.copy()) < 0) {
// new ItemDispenserBehavior().dispense(pointer, potion.copy());
// }

// return stack;
// }