package net.dehydration.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.dehydration.block.CampfireCauldronBlock;
import net.dehydration.init.BlockInit;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
        if (blockState.isOf(BlockInit.CAMPFIRE_CAULDRON_BLOCK) && blockState.get(CampfireCauldronBlock.LEVEL) < 4) {
            BlockPointerImpl blockPointerImpl = new BlockPointerImpl(world, pos);
            DispenserBlockEntity dispenserBlockEntity = (DispenserBlockEntity) blockPointerImpl.getBlockEntity();
            int i = dispenserBlockEntity.chooseNonEmptySlot();
            ItemStack itemStack = dispenserBlockEntity.getStack(i);
            if (i >= 0 && itemStack.getItem().equals(Items.WATER_BUCKET)) {
                itemStack.decrement(1);
                dispenserBlockEntity.setStack(i, new ItemStack(Items.BUCKET));
                ((CampfireCauldronBlock) blockState.getBlock()).setLevel(world, newPos, blockState, 4);
                world.syncWorldEvent(WorldEvents.DISPENSER_DISPENSES, pos, 0);
                info.cancel();
            }
        }
    }

}