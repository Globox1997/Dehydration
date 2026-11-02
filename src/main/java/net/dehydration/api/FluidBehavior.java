package net.dehydration.api;

import net.dehydration.block.CopperLeveledCauldronBlock;
import net.dehydration.block.RainwaterLeveledCollectorBlock;
import net.dehydration.block.entity.CauldronBehaviorAccess;
import net.dehydration.block.entity.CopperCauldronBehavior;
import net.dehydration.block.entity.RainwaterCollectorBehavior;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import java.util.Map;

public class FluidBehavior {

    public static void registerWaterBucketForCauldron(Item emptyBucket, Item filledBucket) {
        Map<Item, CauldronInteraction> waterMap = CauldronInteraction.WATER.map();

        waterMap.put(emptyBucket, (state, world, pos, player, hand, stack) ->
                CauldronBehaviorAccess.fillFromCauldron(
                        state, world, pos, player, hand, stack,
                        new ItemStack(filledBucket),
                        s -> s.getValue(LayeredCauldronBlock.LEVEL) == 3,
                        SoundEvents.BUCKET_FILL
                )
        );
    }

    public static void registerWaterBucketForCopperCauldron(Item emptyBucket, Item filledBucket) {
        CopperCauldronBehavior.WATER_COPPER_CAULDRON_BEHAVIOR.put(
                emptyBucket, (state, world, pos, player, hand, stack) ->
                        CopperCauldronBehavior.emptyCauldron(
                                state, world, pos, player, hand, stack,
                                new ItemStack(filledBucket),
                                s -> s.getValue(CopperLeveledCauldronBlock.LEVEL) == 3,
                                SoundEvents.BUCKET_FILL));
    }

    public static void registerWaterBucketForRainwaterCollector(Item emptyBucket, Item filledBucket) {
        RainwaterCollectorBehavior.WATER_RAINWATER_COLLECTOR_BEHAVIOR.put(
                emptyBucket, (state, world, pos, player, hand, stack) ->
                        RainwaterCollectorBehavior.emptyCollector(
                                state, world, pos, player, hand, stack,
                                new ItemStack(filledBucket),
                                s -> s.getValue(RainwaterLeveledCollectorBlock.LEVEL) == 3,
                                SoundEvents.BUCKET_FILL));
    }

    public static void registerPurifiedWaterBucket(Item emptyBucket, Item filledBucket) {
        // Copper Cauldron
        CopperCauldronBehavior.PURIFIED_WATER_COPPER_CAULDRON_BEHAVIOR.put(
                emptyBucket, (state, world, pos, player, hand, stack) ->
                        CopperCauldronBehavior.emptyCauldron(
                                state, world, pos, player, hand, stack,
                                new ItemStack(filledBucket),
                                s -> s.getValue(CopperLeveledCauldronBlock.LEVEL) == 3, SoundEvents.BUCKET_FILL)
        );

        // Rainwater Collector
        RainwaterCollectorBehavior.PURIFIED_WATER_RAINWATER_COLLECTOR_BEHAVIOR.put(
                emptyBucket, (state, world, pos, player, hand, stack) ->
                        RainwaterCollectorBehavior.emptyCollector(
                                state, world, pos, player, hand, stack,
                                new ItemStack(filledBucket),
                                s -> s.getValue(RainwaterLeveledCollectorBlock.LEVEL) == 3,
                                SoundEvents.BUCKET_FILL));

        // Dispenser
        DispenserBehavior.registerPurifiedBucket(filledBucket, emptyBucket);
    }
}