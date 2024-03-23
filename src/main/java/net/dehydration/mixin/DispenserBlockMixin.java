package net.dehydration.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

import net.dehydration.block.CampfireCauldronBlock;
import net.dehydration.block.CopperLeveledCauldronBlock;
import net.dehydration.block.entity.CampfireCauldronEntity;
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

    @Inject(method = "dispense", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/block/entity/DispenserBlockEntity;getStack(I)Lnet/minecraft/item/ItemStack;"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    protected void dispenseMixin(ServerWorld world, BlockPos pos, CallbackInfo info, BlockPointerImpl blockPointerImpl, DispenserBlockEntity dispenserBlockEntity, int i, ItemStack itemStack) {
        BlockState state = world.getBlockState(pos);
        BlockPos newPos = pos.offset(state.get(DispenserBlock.FACING));
        BlockState blockState = world.getBlockState(newPos);
        if (!itemStack.isEmpty()) {
            if (itemStack.getItem().equals(Items.WATER_BUCKET)) {
                if (blockState.isOf(BlockInit.CAMPFIRE_CAULDRON_BLOCK) && blockState.get(CampfireCauldronBlock.LEVEL) < 4) {
                    itemStack.decrement(1);
                    dispenserBlockEntity.setStack(i, new ItemStack(Items.BUCKET));
                    ((CampfireCauldronEntity) world.getBlockEntity(newPos)).onFillingCauldron();
                    ((CampfireCauldronBlock) blockState.getBlock()).setLevel(world, newPos, blockState, 4);
                    world.syncWorldEvent(WorldEvents.DISPENSER_DISPENSES, pos, 0);
                    info.cancel();
                } else if (blockState.isOf(BlockInit.COPPER_CAULDRON_BLOCK)
                        || (blockState.isOf(BlockInit.COPPER_WATER_CAULDRON_BLOCK) && !((CopperLeveledCauldronBlock) blockState.getBlock()).isFull(blockState))) {
                    itemStack.decrement(1);
                    dispenserBlockEntity.setStack(i, new ItemStack(Items.BUCKET));
                    world.setBlockState(newPos, BlockInit.COPPER_WATER_CAULDRON_BLOCK.getDefaultState().with(CopperLeveledCauldronBlock.LEVEL, 3), 3);
                    world.syncWorldEvent(WorldEvents.DISPENSER_DISPENSES, pos, 0);
                    info.cancel();
                }
            } else if (itemStack.getItem().equals(ItemInit.PURIFIED_BUCKET)) {
                if (blockState.isOf(BlockInit.CAMPFIRE_CAULDRON_BLOCK) && blockState.get(CampfireCauldronBlock.LEVEL) < 4) {
                    itemStack.decrement(1);
                    dispenserBlockEntity.setStack(i, new ItemStack(Items.BUCKET));
                    ((CampfireCauldronEntity) world.getBlockEntity(newPos)).onFillingCauldron();
                    ((CampfireCauldronBlock) blockState.getBlock()).setLevel(world, newPos, blockState, 4);
                    world.syncWorldEvent(WorldEvents.DISPENSER_DISPENSES, pos, 0);
                    info.cancel();
                } else if (blockState.isOf(BlockInit.COPPER_CAULDRON_BLOCK)
                        || (blockState.isOf(BlockInit.COPPER_PURIFIED_WATER_CAULDRON_BLOCK) && !((CopperLeveledCauldronBlock) blockState.getBlock()).isFull(blockState))) {
                    itemStack.decrement(1);
                    dispenserBlockEntity.setStack(i, new ItemStack(Items.BUCKET));
                    world.setBlockState(newPos, BlockInit.COPPER_PURIFIED_WATER_CAULDRON_BLOCK.getDefaultState().with(CopperLeveledCauldronBlock.LEVEL, 3), 3);
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
                } else if (blockState.isOf(BlockInit.COPPER_PURIFIED_WATER_CAULDRON_BLOCK) && !((CopperLeveledCauldronBlock) blockState.getBlock()).isFull(blockState)) {
                    itemStack.decrement(1);
                    dispenserBlockEntity.setStack(i, new ItemStack(Items.GLASS_BOTTLE));
                    world.setBlockState(pos, (BlockState) blockState.cycle(CopperLeveledCauldronBlock.LEVEL));
                    world.syncWorldEvent(WorldEvents.DISPENSER_DISPENSES, pos, 0);
                    info.cancel();
                }
            }
        }
    }

}