package net.dehydration.init;

import net.dehydration.fluid.PurifiedWaterFluid;
import net.dehydration.item.PurifiedBucket;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.EmptyItemFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.FullItemFluidStorage;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class FluidInit {

    public static final FlowableFluid PURIFIED_FLOWING_WATER = register("dehydration:purified_flowing_water", new PurifiedWaterFluid.Flowing());
    public static final FlowableFluid PURIFIED_WATER = register("dehydration:purified_water", new PurifiedWaterFluid.Still());

    public static void init() {
        initFluidStorage();
    }

    private static void initFluidStorage() {
        FluidStorage.GENERAL_COMBINED_PROVIDER.register(context -> {
            if (context.getItemVariant().getItem() instanceof PurifiedBucket bucketItem) {
                Fluid bucketFluid = FluidInit.PURIFIED_WATER;
                if (bucketFluid != null && bucketFluid.getBucketItem() == bucketItem) {
                    return new FullItemFluidStorage(context, Items.BUCKET, FluidVariant.of(bucketFluid), FluidConstants.BUCKET);
                }
            }
            return null;
        });

        FluidStorage.combinedItemApiProvider(Items.BUCKET).register(context -> new EmptyItemFluidStorage(context, ItemInit.PURIFIED_BUCKET, FluidInit.PURIFIED_WATER, FluidConstants.BUCKET));
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
