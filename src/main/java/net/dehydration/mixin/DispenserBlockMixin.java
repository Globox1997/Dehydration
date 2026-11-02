package net.dehydration.mixin;

import net.dehydration.block.RainwaterLeveledCollectorBlock;
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(DispenserBlock.class)
public abstract class DispenserBlockMixin extends BaseEntityBlock {

    public DispenserBlockMixin(Properties settings) {
        super(settings);
    }

    @Inject(method = "dispenseFrom", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/block/entity/DispenserBlockEntity;getItem(I)Lnet/minecraft/world/item/ItemStack;"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void dispenseMixin(ServerLevel world, BlockState state, BlockPos pos, CallbackInfo info, DispenserBlockEntity dispenserBlockEntity, BlockSource blockPointer, int i,
                                 ItemStack itemStack) {
        BlockPos newPos = pos.relative(state.getValue(DispenserBlock.FACING));
        BlockState blockState = world.getBlockState(newPos);
        if (!itemStack.isEmpty()) {
            if (itemStack.getItem().equals(Items.WATER_BUCKET)) {
                if (blockState.is(BlockInit.CAMPFIRE_CAULDRON_BLOCK) && blockState.getValue(CampfireCauldronBlock.LEVEL) < 4) {
                    itemStack.shrink(1);
                    dispenserBlockEntity.setItem(i, new ItemStack(Items.BUCKET));
                    ((CampfireCauldronEntity) world.getBlockEntity(newPos)).onFillingCauldron();
                    ((CampfireCauldronBlock) blockState.getBlock()).setLevel(world, newPos, blockState, 4);
                    world.levelEvent(LevelEvent.SOUND_DISPENSER_DISPENSE, pos, 0);
                    info.cancel();
                } else if (blockState.is(BlockInit.COPPER_CAULDRON_BLOCK)
                        || (blockState.is(BlockInit.COPPER_WATER_CAULDRON_BLOCK) && !((CopperLeveledCauldronBlock) blockState.getBlock()).isFull(blockState))) {
                    itemStack.shrink(1);
                    dispenserBlockEntity.setItem(i, new ItemStack(Items.BUCKET));
                    world.setBlock(newPos, BlockInit.COPPER_WATER_CAULDRON_BLOCK.defaultBlockState().setValue(CopperLeveledCauldronBlock.LEVEL, 3), 3);
                    world.levelEvent(LevelEvent.SOUND_DISPENSER_DISPENSE, pos, 0);
                    info.cancel();
                } else if (blockState.is(BlockInit.RAINWATER_COLLECTOR_BLOCK)
                        || (blockState.is(BlockInit.RAINWATER_WATER_COLLECTOR_BLOCK) && !((RainwaterLeveledCollectorBlock) blockState.getBlock()).isFull(blockState))) {
                    itemStack.shrink(1);
                    dispenserBlockEntity.setItem(i, new ItemStack(Items.BUCKET));
                    world.setBlock(newPos, BlockInit.RAINWATER_WATER_COLLECTOR_BLOCK.defaultBlockState().setValue(RainwaterLeveledCollectorBlock.LEVEL, 3), 3);
                    world.levelEvent(LevelEvent.SOUND_DISPENSER_DISPENSE, pos, 0);
                    info.cancel();
                }
            } else if (itemStack.getItem().equals(ItemInit.PURIFIED_BUCKET)) {
                if (blockState.is(BlockInit.CAMPFIRE_CAULDRON_BLOCK) && blockState.getValue(CampfireCauldronBlock.LEVEL) < 4) {
                    itemStack.shrink(1);
                    dispenserBlockEntity.setItem(i, new ItemStack(Items.BUCKET));
                    ((CampfireCauldronEntity) world.getBlockEntity(newPos)).onFillingCauldron();
                    ((CampfireCauldronBlock) blockState.getBlock()).setLevel(world, newPos, blockState, 4);
                    world.levelEvent(LevelEvent.SOUND_DISPENSER_DISPENSE, pos, 0);
                    info.cancel();
                } else if (blockState.is(BlockInit.COPPER_CAULDRON_BLOCK)
                        || (blockState.is(BlockInit.COPPER_PURIFIED_WATER_CAULDRON_BLOCK) && !((CopperLeveledCauldronBlock) blockState.getBlock()).isFull(blockState))) {
                    itemStack.shrink(1);
                    dispenserBlockEntity.setItem(i, new ItemStack(Items.BUCKET));
                    world.setBlock(newPos, BlockInit.COPPER_PURIFIED_WATER_CAULDRON_BLOCK.defaultBlockState().setValue(CopperLeveledCauldronBlock.LEVEL, 3), 3);
                    world.levelEvent(LevelEvent.SOUND_DISPENSER_DISPENSE, pos, 0);
                    info.cancel();
                } else if (blockState.is(BlockInit.RAINWATER_COLLECTOR_BLOCK)
                        || (blockState.is(BlockInit.RAINWATER_PURIFIED_WATER_COLLECTOR_BLOCK) && !((RainwaterLeveledCollectorBlock) blockState.getBlock()).isFull(blockState))) {
                    itemStack.shrink(1);
                    dispenserBlockEntity.setItem(i, new ItemStack(Items.BUCKET));
                    world.setBlock(newPos, BlockInit.RAINWATER_PURIFIED_WATER_COLLECTOR_BLOCK.defaultBlockState().setValue(RainwaterLeveledCollectorBlock.LEVEL, 3), 3);
                    world.levelEvent(LevelEvent.SOUND_DISPENSER_DISPENSE, pos, 0);
                    info.cancel();
                }
            } else if (itemStack.getItem() instanceof PotionItem && itemStack.get(DataComponents.POTION_CONTENTS) != null && itemStack.get(DataComponents.POTION_CONTENTS).potion().isPresent() && itemStack.get(DataComponents.POTION_CONTENTS).potion().get() == ItemInit.PURIFIED_WATER) {
                if (blockState.is(BlockInit.COPPER_CAULDRON_BLOCK)) {
                    itemStack.shrink(1);
                    dispenserBlockEntity.setItem(i, new ItemStack(Items.GLASS_BOTTLE));
                    world.setBlock(newPos, BlockInit.COPPER_PURIFIED_WATER_CAULDRON_BLOCK.defaultBlockState(), 3);
                    world.levelEvent(LevelEvent.SOUND_DISPENSER_DISPENSE, pos, 0);
                    info.cancel();
                } else if (blockState.is(BlockInit.COPPER_PURIFIED_WATER_CAULDRON_BLOCK) && !((CopperLeveledCauldronBlock) blockState.getBlock()).isFull(blockState)) {
                    itemStack.shrink(1);
                    dispenserBlockEntity.setItem(i, new ItemStack(Items.GLASS_BOTTLE));
                    world.setBlockAndUpdate(newPos, (BlockState) blockState.cycle(CopperLeveledCauldronBlock.LEVEL));
                    world.levelEvent(LevelEvent.SOUND_DISPENSER_DISPENSE, pos, 0);
                    info.cancel();
                }else  if (blockState.is(BlockInit.RAINWATER_COLLECTOR_BLOCK)) {
                    itemStack.shrink(1);
                    dispenserBlockEntity.setItem(i, new ItemStack(Items.GLASS_BOTTLE));
                    world.setBlock(newPos, BlockInit.RAINWATER_PURIFIED_WATER_COLLECTOR_BLOCK.defaultBlockState(), 3);
                    world.levelEvent(LevelEvent.SOUND_DISPENSER_DISPENSE, pos, 0);
                    info.cancel();
                } else if (blockState.is(BlockInit.RAINWATER_PURIFIED_WATER_COLLECTOR_BLOCK) && !((RainwaterLeveledCollectorBlock) blockState.getBlock()).isFull(blockState)) {
                    itemStack.shrink(1);
                    dispenserBlockEntity.setItem(i, new ItemStack(Items.GLASS_BOTTLE));
                    world.setBlockAndUpdate(newPos, (BlockState) blockState.cycle(RainwaterLeveledCollectorBlock.LEVEL));
                    world.levelEvent(LevelEvent.SOUND_DISPENSER_DISPENSE, pos, 0);
                    info.cancel();
                }
            }
        }
    }

}