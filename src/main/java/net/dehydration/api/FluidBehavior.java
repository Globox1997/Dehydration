package net.dehydration.api;

import net.dehydration.block.CopperLeveledCauldronBlock;
import net.dehydration.block.RainwaterLeveledCollectorBlock;
import net.dehydration.block.entity.CauldronBehaviorAccess;
import net.dehydration.block.entity.CopperCauldronBehavior;
import net.dehydration.block.entity.RainwaterCollectorBehavior;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;

import java.util.Map;

public class FluidBehavior {

    public static void registerWaterBucketForCauldron(Item emptyBucket, Item filledBucket) {
        Map<Item, CauldronBehavior> waterMap = CauldronBehavior.WATER_CAULDRON_BEHAVIOR.map();

        waterMap.put(emptyBucket, (state, world, pos, player, hand, stack) ->
                CauldronBehaviorAccess.fillFromCauldron(
                        state, world, pos, player, hand, stack,
                        new ItemStack(filledBucket),
                        s -> s.get(LeveledCauldronBlock.LEVEL) == 3,
                        SoundEvents.ITEM_BUCKET_FILL
                )
        );
    }

    public static void registerWaterBucketForCopperCauldron(Item emptyBucket, Item filledBucket) {
        CopperCauldronBehavior.WATER_COPPER_CAULDRON_BEHAVIOR.put(
                emptyBucket,
                (state, world, pos, player, hand, stack) ->
                        CopperCauldronBehavior.emptyCauldron(
                                state, world, pos, player, hand, stack,
                                new ItemStack(filledBucket),
                                s -> s.get(CopperLeveledCauldronBlock.LEVEL) == 3,
                                SoundEvents.ITEM_BUCKET_FILL
                        )
        );
    }

    public static void registerWaterBucketForRainwaterCollector(Item emptyBucket, Item filledBucket) {
        RainwaterCollectorBehavior.WATER_RAINWATER_COLLECTOR_BEHAVIOR.put(
                emptyBucket,
                (state, world, pos, player, hand, stack) ->
                        RainwaterCollectorBehavior.emptyCollector(
                                state, world, pos, player, hand, stack,
                                new ItemStack(filledBucket),
                                s -> s.get(RainwaterLeveledCollectorBlock.LEVEL) == 3,
                                SoundEvents.ITEM_BUCKET_FILL
                        )
        );
    }

    public static void registerPurifiedWaterBucket(Item emptyBucket, Item filledBucket) {
        // Copper Cauldron
        CopperCauldronBehavior.PURIFIED_WATER_COPPER_CAULDRON_BEHAVIOR.put(
                emptyBucket,
                (state, world, pos, player, hand, stack) ->
                        CopperCauldronBehavior.emptyCauldron(
                                state, world, pos, player, hand, stack,
                                new ItemStack(filledBucket),
                                s -> s.get(CopperLeveledCauldronBlock.LEVEL) == 3,
                                SoundEvents.ITEM_BUCKET_FILL
                        )
        );

        // Rainwater Collector
        RainwaterCollectorBehavior.PURIFIED_WATER_RAINWATER_COLLECTOR_BEHAVIOR.put(
                emptyBucket,
                (state, world, pos, player, hand, stack) ->
                        RainwaterCollectorBehavior.emptyCollector(
                                state, world, pos, player, hand, stack,
                                new ItemStack(filledBucket),
                                s -> s.get(RainwaterLeveledCollectorBlock.LEVEL) == 3,
                                SoundEvents.ITEM_BUCKET_FILL
                        )
        );

        // Dispenser
        DispenserBehavior.registerPurifiedBucket(filledBucket, emptyBucket);
    }
}