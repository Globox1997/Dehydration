package net.dehydration.init;

import net.dehydration.fluid.PurifiedWaterFluid;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class FluidInit {

    public static final FlowableFluid PURIFIED_FLOWING_WATER = register("dehydration:purified_flowing_water", new PurifiedWaterFluid.Flowing());
    public static final FlowableFluid PURIFIED_WATER = register("dehydration:purified_water", new PurifiedWaterFluid.Still());

    public static void init() {
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
