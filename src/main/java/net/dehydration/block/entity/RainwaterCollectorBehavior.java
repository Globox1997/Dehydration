package net.dehydration.block.entity;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.dehydration.block.RainwaterLeveledCollectorBlock;
import net.dehydration.init.BlockInit;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.Map;
import java.util.function.Predicate;

public interface RainwaterCollectorBehavior {
    Map<Item, RainwaterCollectorBehavior> EMPTY_RAINWATER_COLLECTOR_BEHAVIOR = createMap();
    Map<Item, RainwaterCollectorBehavior> WATER_RAINWATER_COLLECTOR_BEHAVIOR = createMap();
    Map<Item, RainwaterCollectorBehavior> POWDER_SNOW_RAINWATER_COLLECTOR_BEHAVIOR = createMap();
    Map<Item, RainwaterCollectorBehavior> PURIFIED_WATER_RAINWATER_COLLECTOR_BEHAVIOR = createMap();

    RainwaterCollectorBehavior FILL_WITH_POWDER_SNOW = (state, world, pos, player, hand, stack) -> fillCollector(world, pos, player, hand, stack, BlockInit.RAINWATER_POWDERED_COLLECTOR_BLOCK.getDefaultState().with(RainwaterLeveledCollectorBlock.LEVEL, 3),
            SoundEvents.ITEM_BUCKET_EMPTY_POWDER_SNOW);

    static Object2ObjectOpenHashMap<Item, RainwaterCollectorBehavior> createMap() {
        return Util.make(new Object2ObjectOpenHashMap<>(), (map) -> map.defaultReturnValue((state, world, pos, player, hand, stack) -> ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION));
    }

    ItemActionResult interact(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack);

    static void registerBehavior() {
        registerBucketBehavior(EMPTY_RAINWATER_COLLECTOR_BEHAVIOR);
        registerBucketBehavior(WATER_RAINWATER_COLLECTOR_BEHAVIOR);
        POWDER_SNOW_RAINWATER_COLLECTOR_BEHAVIOR.put(Items.BUCKET, (state, world, pos, player, hand, stack) -> emptyCollector(state, world, pos, player, hand, stack, new ItemStack(Items.POWDER_SNOW_BUCKET), (statex) -> statex.get(RainwaterLeveledCollectorBlock.LEVEL) == 3, SoundEvents.ITEM_BUCKET_FILL_POWDER_SNOW));
        registerBucketBehavior(POWDER_SNOW_RAINWATER_COLLECTOR_BEHAVIOR);
        registerBucketBehavior(PURIFIED_WATER_RAINWATER_COLLECTOR_BEHAVIOR);
    }

    static void registerBucketBehavior(Map<Item, RainwaterCollectorBehavior> behavior) {
        behavior.put(Items.POWDER_SNOW_BUCKET, FILL_WITH_POWDER_SNOW);
    }

    static ItemActionResult emptyCollector(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, ItemStack output, Predicate<BlockState> predicate, SoundEvent soundEvent) {
        if (!predicate.test(state)) {
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        } else {
            if (!world.isClient()) {
                Item item = stack.getItem();
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, output));
                player.incrementStat(Stats.USE_CAULDRON);
                player.incrementStat(Stats.USED.getOrCreateStat(item));
                world.setBlockState(pos, BlockInit.RAINWATER_COLLECTOR_BLOCK.getDefaultState());
                world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.emitGameEvent(null, GameEvent.FLUID_PICKUP, pos);
            }

            return ItemActionResult.success(world.isClient());
        }
    }

    static ItemActionResult fillCollector(World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, BlockState state, SoundEvent soundEvent) {
        if (!world.isClient()) {
            Item item = stack.getItem();
            player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.BUCKET)));
            player.incrementStat(Stats.FILL_CAULDRON);
            player.incrementStat(Stats.USED.getOrCreateStat(item));
            world.setBlockState(pos, state);
            world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);
            world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
        }

        return ItemActionResult.success(world.isClient());
    }
}
