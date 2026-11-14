package net.dehydration.block.entity;

import net.dehydration.block.CampfireCauldronBlock;
import net.dehydration.block.CopperLeveledCauldronBlock;
import net.dehydration.init.BlockInit;
import net.dehydration.init.ItemInit;
import net.dehydration.item.LeatherFlask;
import net.dehydration.item.component.FlaskComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;

public class DispenserBehaviorAccess {

    public static void registerDefaults() {

        DefaultDispenseItemBehavior itemDispenserBehavior = new OptionalDispenseItemBehavior() {
            @Override
            public ItemStack execute(BlockSource pointer, ItemStack stack) {
                this.setSuccess(false);
                ServerLevel serverWorld = pointer.level();
                BlockPos blockPos = pointer.pos().relative(pointer.state().getValue(DispenserBlock.FACING));
                BlockState blockState = serverWorld.getBlockState(blockPos);
                if (blockState.is(BlockInit.CAMPFIRE_CAULDRON_BLOCK) && blockState.getValue(CampfireCauldronBlock.LEVEL) > 0) {
                    CampfireCauldronBlock campfireCauldronBlock = (CampfireCauldronBlock) blockState.getBlock();

                    if (campfireCauldronBlock.isPurifiedWater(serverWorld, blockPos) && stack.get(ItemInit.FLASK_DATA) != null
                            && stack.getOrDefault(ItemInit.FLASK_DATA, FlaskComponent.DEFAULT).fillLevel() < 2 + ((LeatherFlask) stack.getItem()).getExtraFillLevel()) {
                        this.setSuccess(true);
                        campfireCauldronBlock.setLevel(serverWorld, blockPos, blockState, blockState.getValue(CampfireCauldronBlock.LEVEL) - 1);
                        return getNewFlask(stack, pointer);
                    }
                } else if (blockState.is(BlockInit.COPPER_PURIFIED_WATER_CAULDRON_BLOCK) && blockState.getValue(CopperLeveledCauldronBlock.LEVEL) > 0 && stack.get(ItemInit.FLASK_DATA) != null
                        && stack.getOrDefault(ItemInit.FLASK_DATA, FlaskComponent.DEFAULT).fillLevel() < 2 + ((LeatherFlask) stack.getItem()).getExtraFillLevel()) {
                    this.setSuccess(true);
                    CopperLeveledCauldronBlock.decrementFluidLevel(blockState, serverWorld, blockPos);

                    return getNewFlask(stack, pointer);
                }
                return super.execute(pointer, stack);
            }
        };
        for (int i = 0; i < ItemInit.FLASK_ITEM_LIST.size(); i++) {
            DispenserBlock.registerBehavior(ItemInit.FLASK_ITEM_LIST.get(i), itemDispenserBehavior);
        }
    }

    private static ItemStack getNewFlask(ItemStack stack, BlockSource pointer) {

        FlaskComponent flaskComponent = stack.getOrDefault(ItemInit.FLASK_DATA, FlaskComponent.DEFAULT);

        ItemStack newStack = stack.copy();

        int waterPurity = 0;
        if (flaskComponent.fillLevel() > 0 && flaskComponent.qualityLevel() != 0) {
            waterPurity = 1;
        }

        newStack.set(ItemInit.FLASK_DATA, new FlaskComponent(2 + ((LeatherFlask) newStack.getItem()).getExtraFillLevel(), waterPurity));
        stack.shrink(1);

        if (stack.isEmpty()) {
            return newStack.copy();
        } else {
            if (!pointer.blockEntity().insertItem(newStack.copy()).isEmpty()) {
                new DefaultDispenseItemBehavior().dispense(pointer, newStack.copy());
            }

            return stack;
        }
    }
}