package net.dehydration.init;

import net.dehydration.block.CampfireCauldronBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockInit {

        public static final CampfireCauldronBlock CAMPFIRE_CAULDRON_BLOCK = new CampfireCauldronBlock(
                        FabricBlockSettings.copy(Blocks.CAULDRON));

        public static void init() {
                Registry.register(Registry.ITEM, new Identifier("dehydration", "campfire_cauldron"), new BlockItem(
                                CAMPFIRE_CAULDRON_BLOCK, new Item.Settings().group(ItemGroup.DECORATIONS)));
                Registry.register(Registry.BLOCK, new Identifier("dehydration", "campfire_cauldron"),
                                CAMPFIRE_CAULDRON_BLOCK);
        }

}
