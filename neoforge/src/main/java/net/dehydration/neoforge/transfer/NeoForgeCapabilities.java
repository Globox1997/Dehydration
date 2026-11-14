package net.dehydration.neoforge.transfer;

import net.dehydration.block.CopperLeveledCauldronBlock;
import net.dehydration.block.RainwaterLeveledCollectorBlock;
import net.dehydration.block.entity.CampfireCauldronEntity;
import net.dehydration.init.BlockInit;
import net.dehydration.init.ItemInit;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class NeoForgeCapabilities {

    public static void register(RegisterCapabilitiesEvent event) {
        event.registerItem(Capabilities.Fluid.ITEM, (stack, access) -> new BottleFluidHandler(access), Items.GLASS_BOTTLE, Items.POTION);
        event.registerItem(Capabilities.Fluid.ITEM, (stack, access) -> new LeatherFlaskFluidHandler(access), ItemInit.LEATHER_FLASK, ItemInit.IRON_LEATHER_FLASK, ItemInit.GOLDEN_LEATHER_FLASK,
                ItemInit.DIAMOND_LEATHER_FLASK, ItemInit.NETHERITE_LEATHER_FLASK);
        event.registerItem(Capabilities.Fluid.ITEM, (stack, access) -> new BowlFluidHandler(access), Items.BOWL, ItemInit.WATER_BOWL, ItemInit.PURIFIED_WATER_BOWL);

        event.registerBlock(Capabilities.Fluid.BLOCK,
                (world, pos, state, blockEntity, side) -> blockEntity instanceof CampfireCauldronEntity campfireCauldronEntity ? new CampfireCauldronFluidHandler(world, pos, state, campfireCauldronEntity) : null,
                BlockInit.CAMPFIRE_CAULDRON_BLOCK);
        event.registerBlock(Capabilities.Fluid.BLOCK, (world, pos, state, blockEntity, side) -> new LeveledFluidContainerHandler(world, pos, BlockInit.COPPER_CAULDRON_BLOCK,
                BlockInit.COPPER_WATER_CAULDRON_BLOCK, BlockInit.COPPER_PURIFIED_WATER_CAULDRON_BLOCK, CopperLeveledCauldronBlock.LEVEL), BlockInit.COPPER_CAULDRON_BLOCK,
                BlockInit.COPPER_WATER_CAULDRON_BLOCK, BlockInit.COPPER_PURIFIED_WATER_CAULDRON_BLOCK);
        event.registerBlock(Capabilities.Fluid.BLOCK, (world, pos, state, blockEntity, side) -> new LeveledFluidContainerHandler(world, pos, BlockInit.RAINWATER_COLLECTOR_BLOCK,
                BlockInit.RAINWATER_WATER_COLLECTOR_BLOCK, BlockInit.RAINWATER_PURIFIED_WATER_COLLECTOR_BLOCK, RainwaterLeveledCollectorBlock.LEVEL), BlockInit.RAINWATER_COLLECTOR_BLOCK,
                BlockInit.RAINWATER_WATER_COLLECTOR_BLOCK, BlockInit.RAINWATER_PURIFIED_WATER_COLLECTOR_BLOCK);

        event.registerBlockEntity(Capabilities.Item.BLOCK, BlockInit.BAMBOO_PUMP_ENTITY, (blockEntity, side) -> new BambooPumpItemHandler(blockEntity));
    }

}
