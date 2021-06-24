package net.dehydration.init;

import net.dehydration.block.CampfireCauldronBlock;
import net.dehydration.block.entity.CampfireCauldronEntity;
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
    // Entity
    public static BlockEntityType<CampfireCauldronEntity> CAMPFIRE_CAULDRON_ENTITY;

    public static void init() {
        Registry.register(Registry.ITEM, new Identifier("dehydration", "campfire_cauldron"), new BlockItem(CAMPFIRE_CAULDRON_BLOCK, new Item.Settings().group(ItemGroup.DECORATIONS)));
        Registry.register(Registry.BLOCK, new Identifier("dehydration", "campfire_cauldron"), CAMPFIRE_CAULDRON_BLOCK);
        CAMPFIRE_CAULDRON_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "dehydration:campfire_cauldron_entity",
                FabricBlockEntityTypeBuilder.create(CampfireCauldronEntity::new, CAMPFIRE_CAULDRON_BLOCK).build(null));
    }

}
