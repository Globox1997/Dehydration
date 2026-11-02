package net.dehydration.init;

import net.dehydration.DehydrationMain;
import net.dehydration.block.*;
import net.dehydration.block.entity.BambooPumpEntity;
import net.dehydration.block.entity.CampfireCauldronEntity;
import net.dehydration.block.entity.CopperCauldronBehavior;
import net.dehydration.block.entity.RainwaterCollectorBehavior;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class BlockInit {
    // Block
    public static final Block CAMPFIRE_CAULDRON_BLOCK = register("campfire_cauldron", true,
            new CampfireCauldronBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON).pushReaction(PushReaction.DESTROY)));
    public static final Block COPPER_CAULDRON_BLOCK = register("copper_cauldron", true, new CopperCauldronBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON)));
    public static final Block COPPER_WATER_CAULDRON_BLOCK = register("water_copper_cauldron", false,
            new CopperLeveledCauldronBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON), CopperLeveledCauldronBlock.RAIN_PREDICATE, CopperCauldronBehavior.WATER_COPPER_CAULDRON_BEHAVIOR));
    public static final Block COPPER_POWDERED_CAULDRON_BLOCK = register("powder_snow_copper_cauldron", false,
            new CopperLeveledCauldronBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON), CopperLeveledCauldronBlock.SNOW_PREDICATE, CopperCauldronBehavior.POWDER_SNOW_COPPER_CAULDRON_BEHAVIOR));
    public static final Block COPPER_PURIFIED_WATER_CAULDRON_BLOCK = register("purified_water_copper_cauldron", false,
            new CopperLeveledCauldronBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON), CopperLeveledCauldronBlock.RAIN_PREDICATE, CopperCauldronBehavior.PURIFIED_WATER_COPPER_CAULDRON_BEHAVIOR));
    public static final Block BAMBOO_PUMP_BLOCK = register("bamboo_pump", true,
            new BambooPumpBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).pushReaction(PushReaction.DESTROY).strength(1.2f, 4.0f).sound(SoundType.BAMBOO)));
    public static final Block PURIFIED_WATER = register("purified_water", false, new LiquidBlock(FluidInit.PURIFIED_WATER, BlockBehaviour.Properties.of().mapColor(MapColor.WATER).replaceable()
            .noCollission().strength(100.0f).pushReaction(PushReaction.DESTROY).noLootTable().liquid().sound(SoundType.EMPTY)));

    public static final Block RAINWATER_COLLECTOR_BLOCK = register("rainwater_collector", true, new RainwaterCollectorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL)));
    public static final Block RAINWATER_WATER_COLLECTOR_BLOCK = register("water_rainwater_collector", false,
            new RainwaterLeveledCollectorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL), RainwaterLeveledCollectorBlock.RAIN_PREDICATE, RainwaterCollectorBehavior.WATER_RAINWATER_COLLECTOR_BEHAVIOR));
    public static final Block RAINWATER_POWDERED_COLLECTOR_BLOCK = register("powder_snow_rainwater_collector", false,
            new RainwaterLeveledCollectorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL), RainwaterLeveledCollectorBlock.SNOW_PREDICATE, RainwaterCollectorBehavior.POWDER_SNOW_RAINWATER_COLLECTOR_BEHAVIOR));
    public static final Block RAINWATER_PURIFIED_WATER_COLLECTOR_BLOCK = register("purified_water_rainwater_collector", false,
            new RainwaterLeveledCollectorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL), RainwaterLeveledCollectorBlock.RAIN_PREDICATE, RainwaterCollectorBehavior.PURIFIED_WATER_RAINWATER_COLLECTOR_BEHAVIOR));

    // Entity
    public static BlockEntityType<CampfireCauldronEntity> CAMPFIRE_CAULDRON_ENTITY = BlockEntityType.Builder.of(CampfireCauldronEntity::new, CAMPFIRE_CAULDRON_BLOCK).build(null);
    public static final BlockEntityType<BambooPumpEntity> BAMBOO_PUMP_ENTITY = BlockEntityType.Builder.of(BambooPumpEntity::new, BAMBOO_PUMP_BLOCK).build(null);

    private static Block register(String id, boolean addItemGroup, Block block) {
        return register(DehydrationMain.identifierOf(id), addItemGroup, block);
    }

    private static Block register(ResourceLocation id, boolean addItemGroup, Block block) {
        if (addItemGroup) {
            Item item = Registry.register(BuiltInRegistries.ITEM, id, new BlockItem(block, new Item.Properties()));
            ItemGroupEvents.modifyEntriesEvent(ItemInit.DEHYDRATION_ITEM_GROUP).register(entries -> entries.accept(item));
        }
        return Registry.register(BuiltInRegistries.BLOCK, id, block);
    }

    public static void init() {
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, DehydrationMain.identifierOf("campfire_cauldron_entity"), CAMPFIRE_CAULDRON_ENTITY);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, DehydrationMain.identifierOf("bamboo_pump_entity"), BAMBOO_PUMP_ENTITY);
    }

}
