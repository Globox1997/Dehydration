package net.dehydration.block.entity;

import net.minecraft.world.InteractionResult;
import net.dehydration.init.ItemInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.cauldron.CauldronInteractions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import java.util.Map;
import java.util.function.Predicate;

public class CauldronBehaviorAccess {

    public static void registerBehavior() {
        CauldronInteraction.Dispatcher EMPTY_CAULDRON_BEHAVIOR_MAP = CauldronInteractions.EMPTY;
        CauldronInteraction.Dispatcher WATER_CAULDRON_BEHAVIOR_MAP = CauldronInteractions.WATER;

        CauldronInteraction EMPTY_CAULDRON_DRAIN_BOWL = (state, world, pos, player, hand, stack) -> {
            if (!world.isClientSide()) {
                Item item = stack.getItem();
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.BOWL)));
                player.awardStat(Stats.USE_CAULDRON);
                player.awardStat(Stats.ITEM_USED.get(item));
                world.setBlockAndUpdate(pos, Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 2));
                world.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                world.gameEvent(null, GameEvent.FLUID_PLACE, pos);
            }

            return InteractionResult.SUCCESS;
        };
        EMPTY_CAULDRON_BEHAVIOR_MAP.put(ItemInit.WATER_BOWL, EMPTY_CAULDRON_DRAIN_BOWL);
        EMPTY_CAULDRON_BEHAVIOR_MAP.put(ItemInit.PURIFIED_WATER_BOWL, EMPTY_CAULDRON_DRAIN_BOWL);

        CauldronInteraction WATER_CAULDRON_DRAIN_BOWL = (state, world, pos, player, hand, stack) -> {
            int currentLevel = state.getValue(LayeredCauldronBlock.LEVEL);
            if (LayeredCauldronBlock.MAX_FILL_LEVEL - currentLevel >= 2) {
                if (!world.isClientSide()) {
                    Item item = stack.getItem();
                    player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.BOWL)));
                    player.awardStat(Stats.USE_CAULDRON);
                    player.awardStat(Stats.ITEM_USED.get(item));
                    world.setBlockAndUpdate(pos, state.setValue(LayeredCauldronBlock.LEVEL, currentLevel + 2));
                    world.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                    world.gameEvent(null, GameEvent.FLUID_PLACE, pos);
                }

                return InteractionResult.SUCCESS;
            }
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        };
        WATER_CAULDRON_BEHAVIOR_MAP.put(ItemInit.WATER_BOWL, WATER_CAULDRON_DRAIN_BOWL);
        WATER_CAULDRON_BEHAVIOR_MAP.put(ItemInit.PURIFIED_WATER_BOWL, WATER_CAULDRON_DRAIN_BOWL);

        CauldronInteraction WATER_CAULDRON_FILL_BOWL = (state, world, pos, player, hand, stack) -> {
            int currentLevel = state.getValue(LayeredCauldronBlock.LEVEL);
            if (currentLevel >= 2) {
                if (!world.isClientSide()) {
                    Item item = stack.getItem();
                    player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(ItemInit.WATER_BOWL)));
                    player.awardStat(Stats.USE_CAULDRON);
                    player.awardStat(Stats.ITEM_USED.get(item));
                    if (currentLevel > 2) {
                        world.setBlockAndUpdate(pos, state.setValue(LayeredCauldronBlock.LEVEL, currentLevel - 2));
                    } else {
                        world.setBlockAndUpdate(pos, Blocks.CAULDRON.defaultBlockState());
                    }
                    world.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
                    world.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
                }

                return InteractionResult.SUCCESS;
            }
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        };
        WATER_CAULDRON_BEHAVIOR_MAP.put(Items.BOWL, WATER_CAULDRON_FILL_BOWL);
    }

    public static InteractionResult fillFromCauldron(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack stack, ItemStack output, Predicate<BlockState> predicate, SoundEvent soundEvent) {
        if (!predicate.test(state)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        if (!world.isClientSide()) {
            Item item = stack.getItem();
            player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, output));
            player.awardStat(Stats.USE_CAULDRON);
            player.awardStat(Stats.ITEM_USED.get(item));
            int currentLevel = state.getValue(LayeredCauldronBlock.LEVEL);
            if (currentLevel <= 1) {
                world.setBlockAndUpdate(pos, Blocks.CAULDRON.defaultBlockState());
            } else {
                world.setBlockAndUpdate(pos, state.setValue(LayeredCauldronBlock.LEVEL, currentLevel - 1));
            }
            world.playSound(null, pos, soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
            world.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
        }
        return InteractionResult.SUCCESS;
    }
}
