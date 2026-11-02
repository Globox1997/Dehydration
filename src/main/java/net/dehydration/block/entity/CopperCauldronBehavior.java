package net.dehydration.block.entity;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.dehydration.block.CopperLeveledCauldronBlock;
import net.dehydration.init.BlockInit;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import java.util.Map;
import java.util.function.Predicate;

public interface CopperCauldronBehavior {
    Map<Item, CopperCauldronBehavior> EMPTY_COPPER_CAULDRON_BEHAVIOR = createMap();
    Map<Item, CopperCauldronBehavior> WATER_COPPER_CAULDRON_BEHAVIOR = createMap();
    Map<Item, CopperCauldronBehavior> POWDER_SNOW_COPPER_CAULDRON_BEHAVIOR = createMap();
    Map<Item, CopperCauldronBehavior> PURIFIED_WATER_COPPER_CAULDRON_BEHAVIOR = createMap();

    CopperCauldronBehavior FILL_WITH_POWDER_SNOW = (state, world, pos, player, hand, stack) -> fillCauldron(world, pos, player, hand, stack, BlockInit.COPPER_POWDERED_CAULDRON_BLOCK.defaultBlockState().setValue(CopperLeveledCauldronBlock.LEVEL, 3),
            SoundEvents.BUCKET_EMPTY_POWDER_SNOW);

    static Object2ObjectOpenHashMap<Item, CopperCauldronBehavior> createMap() {
        return Util.make(new Object2ObjectOpenHashMap<>(), (map) -> {
            map.defaultReturnValue((state, world, pos, player, hand, stack) -> ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION);
        });
    }

    ItemInteractionResult interact(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack stack);

    static void registerBehavior() {
        registerBucketBehavior(EMPTY_COPPER_CAULDRON_BEHAVIOR);
        registerBucketBehavior(WATER_COPPER_CAULDRON_BEHAVIOR);
        POWDER_SNOW_COPPER_CAULDRON_BEHAVIOR.put(Items.BUCKET, (state, world, pos, player, hand, stack) -> emptyCauldron(state, world, pos, player, hand, stack, new ItemStack(Items.POWDER_SNOW_BUCKET), (statex) -> statex.getValue(CopperLeveledCauldronBlock.LEVEL) == 3, SoundEvents.BUCKET_FILL_POWDER_SNOW));
        registerBucketBehavior(POWDER_SNOW_COPPER_CAULDRON_BEHAVIOR);
        registerBucketBehavior(PURIFIED_WATER_COPPER_CAULDRON_BEHAVIOR);
    }

    static void registerBucketBehavior(Map<Item, CopperCauldronBehavior> behavior) {
        behavior.put(Items.POWDER_SNOW_BUCKET, FILL_WITH_POWDER_SNOW);
    }

    static ItemInteractionResult emptyCauldron(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack stack, ItemStack output, Predicate<BlockState> predicate, SoundEvent soundEvent) {
        if (!predicate.test(state)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        } else {
            if (!world.isClientSide()) {
                Item item = stack.getItem();
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, output));
                player.awardStat(Stats.USE_CAULDRON);
                player.awardStat(Stats.ITEM_USED.get(item));
                world.setBlockAndUpdate(pos, BlockInit.COPPER_CAULDRON_BLOCK.defaultBlockState());
                world.playSound(null, pos, soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
                world.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
            }

            return ItemInteractionResult.sidedSuccess(world.isClientSide());
        }
    }

    static ItemInteractionResult fillCauldron(Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack stack, BlockState state, SoundEvent soundEvent) {
        if (!world.isClientSide()) {
            Item item = stack.getItem();
            player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.BUCKET)));
            player.awardStat(Stats.FILL_CAULDRON);
            player.awardStat(Stats.ITEM_USED.get(item));
            world.setBlockAndUpdate(pos, state);
            world.playSound(null, pos, soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
            world.gameEvent(null, GameEvent.FLUID_PLACE, pos);
        }

        return ItemInteractionResult.sidedSuccess(world.isClientSide());
    }
}
