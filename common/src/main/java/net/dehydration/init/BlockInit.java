package net.dehydration.init;

import net.dehydration.DehydrationMain;
import net.dehydration.platform.Services;
import net.dehydration.block.*;
import net.dehydration.block.entity.BambooPumpEntity;
import net.dehydration.block.entity.CampfireCauldronEntity;
import net.dehydration.block.entity.CopperCauldronBehavior;
import net.dehydration.block.entity.RainwaterCollectorBehavior;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class BlockInit {
    private static final Map<Identifier, Block> BLOCKS = new LinkedHashMap<>();
    private static final List<Identifier> BLOCK_ITEMS = new ArrayList<>();

    // Block
    public static final Block CAMPFIRE_CAULDRON_BLOCK = register("campfire_cauldron", true, CampfireCauldronBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON).pushReaction(PushReaction.POPPED));
    public static final Block COPPER_CAULDRON_BLOCK = register("copper_cauldron", true, CopperCauldronBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON));
    public static final Block COPPER_WATER_CAULDRON_BLOCK = register("water_copper_cauldron", false, settings -> new CopperLeveledCauldronBlock(settings, CopperLeveledCauldronBlock.RAIN_PREDICATE, CopperCauldronBehavior.WATER_COPPER_CAULDRON_BEHAVIOR), BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON));
    public static final Block COPPER_POWDERED_CAULDRON_BLOCK = register("powder_snow_copper_cauldron", false, settings -> new CopperLeveledCauldronBlock(settings, CopperLeveledCauldronBlock.SNOW_PREDICATE, CopperCauldronBehavior.POWDER_SNOW_COPPER_CAULDRON_BEHAVIOR), BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON));
    public static final Block COPPER_PURIFIED_WATER_CAULDRON_BLOCK = register("purified_water_copper_cauldron", false, settings -> new CopperLeveledCauldronBlock(settings, CopperLeveledCauldronBlock.RAIN_PREDICATE, CopperCauldronBehavior.PURIFIED_WATER_COPPER_CAULDRON_BEHAVIOR), BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON));
    public static final Block BAMBOO_PUMP_BLOCK = register("bamboo_pump", true, BambooPumpBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).pushReaction(PushReaction.POPPED).strength(1.2f, 4.0f).sound(SoundType.BAMBOO));
    public static final Block PURIFIED_WATER = register("purified_water", false, settings -> Services.fluid().createPurifiedWaterBlock(FluidInit.PURIFIED_WATER, settings), BlockBehaviour.Properties.of().mapColor(MapColor.WATER).replaceable()
            .noCollision().strength(100.0f).pushReaction(PushReaction.POPPED).noLootTable().liquid().sound(SoundType.EMPTY));

    public static final Block RAINWATER_COLLECTOR_BLOCK = register("rainwater_collector", true, RainwaterCollectorBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL));
    public static final Block RAINWATER_WATER_COLLECTOR_BLOCK = register("water_rainwater_collector", false, settings -> new RainwaterLeveledCollectorBlock(settings, RainwaterLeveledCollectorBlock.RAIN_PREDICATE, RainwaterCollectorBehavior.WATER_RAINWATER_COLLECTOR_BEHAVIOR), BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL));
    public static final Block RAINWATER_POWDERED_COLLECTOR_BLOCK = register("powder_snow_rainwater_collector", false, settings -> new RainwaterLeveledCollectorBlock(settings, RainwaterLeveledCollectorBlock.SNOW_PREDICATE, RainwaterCollectorBehavior.POWDER_SNOW_RAINWATER_COLLECTOR_BEHAVIOR), BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL));
    public static final Block RAINWATER_PURIFIED_WATER_COLLECTOR_BLOCK = register("purified_water_rainwater_collector", false, settings -> new RainwaterLeveledCollectorBlock(settings, RainwaterLeveledCollectorBlock.RAIN_PREDICATE, RainwaterCollectorBehavior.PURIFIED_WATER_RAINWATER_COLLECTOR_BEHAVIOR), BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL));

    // Entity
    public static BlockEntityType<CampfireCauldronEntity> CAMPFIRE_CAULDRON_ENTITY = new BlockEntityType<>(CampfireCauldronEntity::new, Set.of(CAMPFIRE_CAULDRON_BLOCK));
    public static final BlockEntityType<BambooPumpEntity> BAMBOO_PUMP_ENTITY = new BlockEntityType<>(BambooPumpEntity::new, Set.of(BAMBOO_PUMP_BLOCK));

    private static Block register(String id, boolean addItemGroup, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties settings) {
        return register(DehydrationMain.identifierOf(id), addItemGroup, factory, settings);
    }

    private static Block register(Identifier id, boolean addItemGroup, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties settings) {
        Block block = factory.apply(settings.setId(ResourceKey.create(Registries.BLOCK, id)));
        BLOCKS.put(id, block);
        if (addItemGroup) {
            BLOCK_ITEMS.add(id);
        }
        return block;
    }

    public static void registerBlocks(BiConsumer<Identifier, Block> registrar) {
        BLOCKS.forEach(registrar);
    }

    public static void registerBlockItems(BiConsumer<Identifier, Item> registrar) {
        for (Identifier id : BLOCK_ITEMS) {
            Item item = new BlockItem(BLOCKS.get(id), new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id)).useBlockDescriptionPrefix());
            ItemInit.ITEM_GROUP_ITEMS.add(item);
            registrar.accept(id, item);
        }
    }

    public static void registerBlockEntityTypes(BiConsumer<Identifier, BlockEntityType<?>> registrar) {
        registrar.accept(DehydrationMain.identifierOf("campfire_cauldron_entity"), CAMPFIRE_CAULDRON_ENTITY);
        registrar.accept(DehydrationMain.identifierOf("bamboo_pump_entity"), BAMBOO_PUMP_ENTITY);
    }

}
