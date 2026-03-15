package net.dehydration.init;

import net.dehydration.block.entity.CampfireCauldronEntity;
import net.dehydration.fluid.PurifiedWaterFluid;
import net.dehydration.fluid.storage.*;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.EmptyItemFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class FluidInit {

    public static final FlowableFluid PURIFIED_FLOWING_WATER = register("dehydration:purified_flowing_water", new PurifiedWaterFluid.Flowing());
    public static final FlowableFluid PURIFIED_WATER = register("dehydration:purified_water", new PurifiedWaterFluid.Still());

    public static void init() {
        // BucketItem storages are added by Fabric
        // Register empty bottle storage for purified water
        FluidStorage.combinedItemApiProvider(Items.GLASS_BOTTLE).register(context -> new EmptyItemFluidStorage(context, emptyBottle -> {
            ItemStack newStack = emptyBottle.toStack();
            newStack.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(ItemInit.PURIFIED_WATER));
            return ItemVariant.of(Items.POTION, newStack.getComponentChanges());
        }, FluidInit.PURIFIED_WATER, FluidConstants.BOTTLE));
        // Register purified water potion storage
        FluidStorage.combinedItemApiProvider(Items.POTION).register(PurifiedWaterPotionStorage::find);
        // Register flask storage
        FluidStorage.ITEM.registerForItems((itemStack, context) -> new LeatherFlaskFluidStorage(context), ItemInit.LEATHER_FLASK, ItemInit.IRON_LEATHER_FLASK, ItemInit.GOLDEN_LEATHER_FLASK, ItemInit.DIAMOND_LEATHER_FLASK, ItemInit.NETHERITE_LEATHER_FLASK);
        // Register bowl storage
        FluidStorage.ITEM.registerForItems(BowlFluidStorage::new, Items.BOWL, ItemInit.WATER_BOWL, ItemInit.PURIFIED_WATER_BOWL);
        // Register campfire cauldron storage
        FluidStorage.SIDED.registerForBlocks((world, pos, state, blockEntity, context) -> new CampfireCauldronFluidStorage(world, pos, state, (CampfireCauldronEntity) blockEntity), BlockInit.CAMPFIRE_CAULDRON_BLOCK);
        //register copper cauldron fluid storage
        FluidStorage.SIDED.registerForBlocks((world, pos, state, be, context) -> new CopperCauldronFluidStorage(world, pos), BlockInit.COPPER_CAULDRON_BLOCK, BlockInit.COPPER_WATER_CAULDRON_BLOCK, BlockInit.COPPER_PURIFIED_WATER_CAULDRON_BLOCK);
        //register rainwater collector fluid storage
        FluidStorage.SIDED.registerForBlocks((world, pos, state, be, context) -> new RainwaterCollectorFluidStorage(world, pos), BlockInit.RAINWATER_COLLECTOR_BLOCK, BlockInit.RAINWATER_WATER_COLLECTOR_BLOCK, BlockInit.RAINWATER_PURIFIED_WATER_COLLECTOR_BLOCK);
    }

    private static <T extends Fluid> T register(String id, T value) {
        return (T) Registry.register(Registries.FLUID, id, value);
    }

    static {
        for (Fluid fluid : Registries.FLUID) {
            for (FluidState fluidState : fluid.getStateManager().getStates()) {
                Fluid.STATE_IDS.add(fluidState);
            }
        }
    }
}
