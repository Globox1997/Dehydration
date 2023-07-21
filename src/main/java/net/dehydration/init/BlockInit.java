package net.dehydration.init;

import net.dehydration.block.*;
import net.dehydration.block.entity.*;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class BlockInit {
    // Block
    public static final Block CAMPFIRE_CAULDRON_BLOCK = register("campfire_cauldron", true,
            new CampfireCauldronBlock(FabricBlockSettings.copy(Blocks.CAULDRON).pistonBehavior(PistonBehavior.DESTROY)));
    public static final Block COPPER_CAULDRON_BLOCK = register("copper_cauldron", true, new CopperCauldronBlock(FabricBlockSettings.copy(Blocks.CAULDRON)));
    public static final Block COPPER_WATER_CAULDRON_BLOCK = register("water_copper_cauldron", false,
            new CopperLeveledCauldronBlock(FabricBlockSettings.copy(Blocks.CAULDRON), CopperLeveledCauldronBlock.RAIN_PREDICATE, CopperCauldronBehavior.WATER_COPPER_CAULDRON_BEHAVIOR));
    public static final Block COPPER_POWDERED_CAULDRON_BLOCK = register("powder_snow_copper_cauldron", false,
            new CopperLeveledCauldronBlock(FabricBlockSettings.copy(Blocks.CAULDRON), CopperLeveledCauldronBlock.SNOW_PREDICATE, CopperCauldronBehavior.POWDER_SNOW_COPPER_CAULDRON_BEHAVIOR));
    public static final Block COPPER_PURIFIED_WATER_CAULDRON_BLOCK = register("purified_water_copper_cauldron", false,
            new CopperLeveledCauldronBlock(FabricBlockSettings.copy(Blocks.CAULDRON), CopperLeveledCauldronBlock.RAIN_PREDICATE, CopperCauldronBehavior.PURIFIED_WATER_COPPER_CAULDRON_BEHAVIOR));
    public static final Block BAMBOO_PUMP_BLOCK = register("bamboo_pump", true,
            new BambooPumpBlock(FabricBlockSettings.create().mapColor(MapColor.DARK_GREEN).pistonBehavior(PistonBehavior.DESTROY).strength(1.2f, 4.0f).sounds(BlockSoundGroup.BAMBOO)));
    public static final Block PURIFIED_WATER = register("purified_water", false, new FluidBlock(FluidInit.PURIFIED_WATER, AbstractBlock.Settings.create().mapColor(MapColor.WATER_BLUE).noCollision()
            .strength(100.0f).pistonBehavior(PistonBehavior.DESTROY).dropsNothing().liquid().sounds(BlockSoundGroup.INTENTIONALLY_EMPTY)));

    // Entity
    public static BlockEntityType<CampfireCauldronEntity> CAMPFIRE_CAULDRON_ENTITY = FabricBlockEntityTypeBuilder.create(CampfireCauldronEntity::new, CAMPFIRE_CAULDRON_BLOCK).build(null);
    public static final BlockEntityType<BambooPumpEntity> BAMBOO_PUMP_ENTITY = FabricBlockEntityTypeBuilder.create(BambooPumpEntity::new, BAMBOO_PUMP_BLOCK).build(null);

    private static Block register(String id, boolean addItemGroup, Block block) {
        return register(new Identifier("dehydration", id), addItemGroup, block);
    }

    private static Block register(Identifier id, boolean addItemGroup, Block block) {
        if (addItemGroup) {
            Item item = Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings()));
            ItemGroupEvents.modifyEntriesEvent(ItemInit.DEHYDRATION_ITEM_GROUP).register(entries -> entries.add(item));
        }
        return Registry.register(Registries.BLOCK, id, block);
    }

    public static void init() {
        Registry.register(Registries.BLOCK_ENTITY_TYPE, "dehydration:campfire_cauldron_entity", CAMPFIRE_CAULDRON_ENTITY);
        Registry.register(Registries.BLOCK_ENTITY_TYPE, "dehydration:bamboo_pump_entity", BAMBOO_PUMP_ENTITY);
    }

}
