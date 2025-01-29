package net.dehydration.block.entity;

import net.dehydration.block.CampfireCauldronBlock;
import net.dehydration.block.CopperLeveledCauldronBlock;
import net.dehydration.init.BlockInit;
import net.dehydration.init.ItemInit;
import net.dehydration.item.LeatherFlask;
import net.dehydration.item.component.FlaskComponent;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;

public class DispenserBehaviorAccess {

    public static void registerDefaults() {

        ItemDispenserBehavior itemDispenserBehavior = new FallibleItemDispenserBehavior() {
            @Override
            public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                this.setSuccess(false);
                ServerWorld serverWorld = pointer.world();
                BlockPos blockPos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING));
                BlockState blockState = serverWorld.getBlockState(blockPos);
                if (blockState.isOf(BlockInit.CAMPFIRE_CAULDRON_BLOCK) && blockState.get(CampfireCauldronBlock.LEVEL) > 0) {
                    CampfireCauldronBlock campfireCauldronBlock = (CampfireCauldronBlock) blockState.getBlock();

                    if (campfireCauldronBlock.isPurifiedWater(serverWorld, blockPos) && stack.get(ItemInit.FLASK_DATA) != null
                            && stack.getOrDefault(ItemInit.FLASK_DATA, FlaskComponent.DEFAULT).fillLevel() < 2 + ((LeatherFlask) stack.getItem()).getExtraFillLevel()) {
                        this.setSuccess(true);
                        campfireCauldronBlock.setLevel(serverWorld, blockPos, blockState, blockState.get(CampfireCauldronBlock.LEVEL) - 1);
                        return getNewFlask(stack, pointer);
                    }
                } else if (blockState.isOf(BlockInit.COPPER_PURIFIED_WATER_CAULDRON_BLOCK) && blockState.get(CopperLeveledCauldronBlock.LEVEL) > 0 && stack.get(ItemInit.FLASK_DATA) != null
                        && stack.getOrDefault(ItemInit.FLASK_DATA, FlaskComponent.DEFAULT).fillLevel() < 2 + ((LeatherFlask) stack.getItem()).getExtraFillLevel()) {
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

        FlaskComponent flaskComponent = stack.getOrDefault(ItemInit.FLASK_DATA, FlaskComponent.DEFAULT);

        ItemStack newStack = stack.copy();

        int waterPurity = 0;
        if (flaskComponent.fillLevel() > 0 && flaskComponent.qualityLevel() != 0) {
            waterPurity = 1;
        }

        newStack.set(ItemInit.FLASK_DATA, new FlaskComponent(2 + ((LeatherFlask) newStack.getItem()).getExtraFillLevel(), waterPurity));
        stack.decrement(1);

        if (stack.isEmpty()) {
            return newStack.copy();
        } else {
            if (!pointer.blockEntity().addToFirstFreeSlot(newStack.copy()).isEmpty()) {
                new ItemDispenserBehavior().dispense(pointer, newStack.copy());
            }

            return stack;
        }
    }
}