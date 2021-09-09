package net.dehydration.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.dehydration.block.CampfireCauldronBlock;
import net.dehydration.block.CopperLeveledCauldronBlock;
import net.dehydration.init.BlockInit;
import net.dehydration.init.ItemInit;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.PotionUtil;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointerImpl;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldEvents;

@Mixin(DispenserBlock.class)
public abstract class DispenserBlockMixin extends BlockWithEntity {

    public DispenserBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "scheduledTick", at = @At(value = "HEAD"), cancellable = true)
    public void scheduledTickMixin(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo info) {
        BlockPos newPos = pos.offset(state.get(DispenserBlock.FACING));
        BlockState blockState = world.getBlockState(newPos);
        BlockPointerImpl blockPointerImpl = new BlockPointerImpl(world, pos);
        DispenserBlockEntity dispenserBlockEntity = (DispenserBlockEntity) blockPointerImpl.getBlockEntity();
        int i = dispenserBlockEntity.chooseNonEmptySlot();
        if (i >= 0) {
            ItemStack itemStack = dispenserBlockEntity.getStack(i);
            if (!itemStack.isEmpty()) {
                if (itemStack.getItem().equals(Items.WATER_BUCKET)) {
                    if (blockState.isOf(BlockInit.CAMPFIRE_CAULDRON_BLOCK) && blockState.get(CampfireCauldronBlock.LEVEL) < 4) {
                        itemStack.decrement(1);
                        dispenserBlockEntity.setStack(i, new ItemStack(Items.BUCKET));
                        ((CampfireCauldronBlock) blockState.getBlock()).setLevel(world, newPos, blockState, 4);
                        world.syncWorldEvent(WorldEvents.DISPENSER_DISPENSES, pos, 0);
                        info.cancel();
                    } else if (blockState.isOf(BlockInit.COPPER_CAULDRON_BLOCK)
                            || (blockState.isOf(BlockInit.COPPER_WATER_CAULDRON_BLOCK) && !((CopperLeveledCauldronBlock) state.getBlock()).isFull(state))) {
                        itemStack.decrement(1);
                        dispenserBlockEntity.setStack(i, new ItemStack(Items.BUCKET));
                        world.setBlockState(newPos, BlockInit.COPPER_WATER_CAULDRON_BLOCK.getDefaultState().with(CopperLeveledCauldronBlock.LEVEL, 3), 3);
                        world.syncWorldEvent(WorldEvents.DISPENSER_DISPENSES, pos, 0);
                        info.cancel();
                    }
                } else if (itemStack.getItem() instanceof PotionItem && PotionUtil.getPotion(itemStack) == ItemInit.PURIFIED_WATER) {
                    if (blockState.isOf(BlockInit.COPPER_CAULDRON_BLOCK)) {
                        itemStack.decrement(1);
                        dispenserBlockEntity.setStack(i, new ItemStack(Items.GLASS_BOTTLE));
                        world.setBlockState(newPos, BlockInit.COPPER_PURIFIED_WATER_CAULDRON_BLOCK.getDefaultState(), 3);
                        world.syncWorldEvent(WorldEvents.DISPENSER_DISPENSES, pos, 0);
                        info.cancel();
                    } else if (blockState.isOf(BlockInit.COPPER_PURIFIED_WATER_CAULDRON_BLOCK) && !((CopperLeveledCauldronBlock) state.getBlock()).isFull(state)) {
                        itemStack.decrement(1);
                        dispenserBlockEntity.setStack(i, new ItemStack(Items.GLASS_BOTTLE));
                        world.setBlockState(pos, (BlockState) state.cycle(CopperLeveledCauldronBlock.LEVEL));
                        world.syncWorldEvent(WorldEvents.DISPENSER_DISPENSES, pos, 0);
                        info.cancel();
                    }
                }
            }
        }
    }

}