package net.dehydration.init;

import net.dehydration.block.*;
import net.dehydration.block.entity.*;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockInit {
    // Block
    public static final CampfireCauldronBlock CAMPFIRE_CAULDRON_BLOCK = new CampfireCauldronBlock(FabricBlockSettings.copy(Blocks.CAULDRON));
    public static final CopperCauldronBlock COPPER_CAULDRON_BLOCK = new CopperCauldronBlock(FabricBlockSettings.copy(Blocks.CAULDRON));
    public static final CopperLeveledCauldronBlock COPPER_WATER_CAULDRON_BLOCK = new CopperLeveledCauldronBlock(FabricBlockSettings.copy(Blocks.CAULDRON), CopperLeveledCauldronBlock.RAIN_PREDICATE,
            CopperCauldronBehavior.WATER_COPPER_CAULDRON_BEHAVIOR);
    public static final CopperLeveledCauldronBlock COPPER_POWDERED_CAULDRON_BLOCK = new CopperLeveledCauldronBlock(FabricBlockSettings.copy(Blocks.CAULDRON),
            CopperLeveledCauldronBlock.SNOW_PREDICATE, CopperCauldronBehavior.POWDER_SNOW_COPPER_CAULDRON_BEHAVIOR);
    public static final CopperLeveledCauldronBlock COPPER_PURIFIED_WATER_CAULDRON_BLOCK = new CopperLeveledCauldronBlock(FabricBlockSettings.copy(Blocks.CAULDRON),
            CopperLeveledCauldronBlock.RAIN_PREDICATE, CopperCauldronBehavior.PURIFIED_WATER_COPPER_CAULDRON_BEHAVIOR);
    // Entity
    public static BlockEntityType<CampfireCauldronEntity> CAMPFIRE_CAULDRON_ENTITY;

    public static void init() {
        Registry.register(Registry.ITEM, new Identifier("dehydration", "campfire_cauldron"), new BlockItem(CAMPFIRE_CAULDRON_BLOCK, new Item.Settings().group(ItemGroup.DECORATIONS)));
        Registry.register(Registry.BLOCK, new Identifier("dehydration", "campfire_cauldron"), CAMPFIRE_CAULDRON_BLOCK);
        CAMPFIRE_CAULDRON_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "dehydration:campfire_cauldron_entity",
                FabricBlockEntityTypeBuilder.create(CampfireCauldronEntity::new, CAMPFIRE_CAULDRON_BLOCK).build(null));
        Registry.register(Registry.ITEM, new Identifier("dehydration", "copper_cauldron"), new BlockItem(COPPER_CAULDRON_BLOCK, new Item.Settings().group(ItemGroup.DECORATIONS)));
        Registry.register(Registry.BLOCK, new Identifier("dehydration", "copper_cauldron"), COPPER_CAULDRON_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier("dehydration", "water_copper_cauldron"), COPPER_WATER_CAULDRON_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier("dehydration", "powder_snow_copper_cauldron"), COPPER_POWDERED_CAULDRON_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier("dehydration", "purified_water_copper_cauldron"), COPPER_PURIFIED_WATER_CAULDRON_BLOCK);
    }

}
