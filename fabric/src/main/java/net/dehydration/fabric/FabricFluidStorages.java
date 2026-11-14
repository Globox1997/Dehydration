package net.dehydration.fabric;

import net.dehydration.block.entity.BambooPumpEntity;
import net.dehydration.block.entity.CampfireCauldronEntity;
import net.dehydration.fluid.storage.*;
import net.dehydration.init.BlockInit;
import net.dehydration.init.FluidInit;
import net.dehydration.init.ItemInit;
import net.dehydration.item.storage.BambooPumpItemStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.EmptyItemFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;

public class FabricFluidStorages {

    public static void register() {
        // BucketItem storages are added by Fabric
        // Register empty bottle storage for purified water
        FluidStorage.combinedItemApiProvider(Items.GLASS_BOTTLE).register(context -> new EmptyItemFluidStorage(context, emptyBottle -> {
            ItemStack newStack = emptyBottle.toStack();
            newStack.set(DataComponents.POTION_CONTENTS, new PotionContents(ItemInit.PURIFIED_WATER));
            return ItemVariant.of(Items.POTION, newStack.getComponentsPatch());
        }, FluidInit.PURIFIED_WATER, FluidConstants.BOTTLE));
        // Register purified water potion storage
        FluidStorage.combinedItemApiProvider(Items.POTION).register(PurifiedWaterPotionStorage::find);
        // Register flask storage
        FluidStorage.ITEM.registerForItems((itemStack, context) -> new LeatherFlaskFluidStorage(context), ItemInit.LEATHER_FLASK, ItemInit.IRON_LEATHER_FLASK, ItemInit.GOLDEN_LEATHER_FLASK, ItemInit.DIAMOND_LEATHER_FLASK, ItemInit.NETHERITE_LEATHER_FLASK);
        // Register bowl storage
        FluidStorage.ITEM.registerForItems(BowlFluidStorage::new, Items.BOWL, ItemInit.WATER_BOWL, ItemInit.PURIFIED_WATER_BOWL);
        // Register campfire cauldron storage
        FluidStorage.SIDED.registerForBlocks((world, pos, state, blockEntity, context) -> blockEntity instanceof CampfireCauldronEntity campfireCauldronEntity ? new CampfireCauldronFluidStorage(world, pos, state, campfireCauldronEntity) : null, BlockInit.CAMPFIRE_CAULDRON_BLOCK);
        //register copper cauldron fluid storage
        FluidStorage.SIDED.registerForBlocks((world, pos, state, be, context) -> new CopperCauldronFluidStorage(world, pos), BlockInit.COPPER_CAULDRON_BLOCK, BlockInit.COPPER_WATER_CAULDRON_BLOCK, BlockInit.COPPER_PURIFIED_WATER_CAULDRON_BLOCK);
        //register rainwater collector fluid storage
        FluidStorage.SIDED.registerForBlocks((world, pos, state, be, context) -> new RainwaterCollectorFluidStorage(world, pos), BlockInit.RAINWATER_COLLECTOR_BLOCK, BlockInit.RAINWATER_WATER_COLLECTOR_BLOCK, BlockInit.RAINWATER_PURIFIED_WATER_COLLECTOR_BLOCK);
        // register item storage for bamboo pump
        ItemStorage.SIDED.registerForBlockEntities((blockEntity, context) -> new BambooPumpItemStorage((BambooPumpEntity) blockEntity), BlockInit.BAMBOO_PUMP_ENTITY);
    }

}
