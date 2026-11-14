package net.dehydration.init;

import java.util.function.BiConsumer;

import net.dehydration.platform.Services;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;

public class FluidInit {

    public static final FlowingFluid PURIFIED_FLOWING_WATER = Services.fluid().createPurifiedWaterFluid(false);
    public static final FlowingFluid PURIFIED_WATER = Services.fluid().createPurifiedWaterFluid(true);

    public static void register(BiConsumer<Identifier, Fluid> registrar) {
        registrar.accept(Identifier.parse("dehydration:purified_flowing_water"), PURIFIED_FLOWING_WATER);
        registrar.accept(Identifier.parse("dehydration:purified_water"), PURIFIED_WATER);
    }

}
