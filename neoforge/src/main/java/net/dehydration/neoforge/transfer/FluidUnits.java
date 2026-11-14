package net.dehydration.neoforge.transfer;

import net.dehydration.init.FluidInit;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

public final class FluidUnits {

    public static final int BOTTLE = 250;
    public static final int BUCKET = FluidType.BUCKET_VOLUME;
    public static final int BUCKET_LEVELS = 3;

    private FluidUnits() {
    }

    public static Transfer transfer(int requestedAmount, int availableLevels) {
        if (requestedAmount >= BUCKET && availableLevels >= BUCKET_LEVELS) {
            return new Transfer(BUCKET_LEVELS, BUCKET);
        }
        int levels = Math.max(0, Math.min(requestedAmount / BOTTLE, availableLevels));
        return new Transfer(levels, levels * BOTTLE);
    }

    public static boolean isWater(FluidResource resource) {
        return resource.isComponentsPatchEmpty() && (resource.getFluid() == Fluids.WATER || resource.getFluid() == FluidInit.PURIFIED_WATER);
    }

    public record Transfer(int levels, int amount) {
    }
}
