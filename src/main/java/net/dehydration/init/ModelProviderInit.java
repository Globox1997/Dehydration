package net.dehydration.init;

import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class ModelProviderInit {

    public static void init() {
        FabricModelPredicateProviderRegistry.register(ItemInit.LEATHER_FLASK, new Identifier("empty"), (stack, world, entity, seed) -> {
            NbtCompound tags = stack.getNbt();
            if (entity == null || (stack.hasNbt() && tags.getInt("leather_flask") == 0)) {
                return 1.0F;
            } else {
                return 0.0F;
            }
        });
        FabricModelPredicateProviderRegistry.register(ItemInit.IRON_LEATHER_FLASK, new Identifier("empty"), (stack, world, entity, seed) -> {
            NbtCompound tags = stack.getNbt();
            if (entity == null || (stack.hasNbt() && tags.getInt("leather_flask") == 0)) {
                return 1.0F;
            } else {
                return 0.0F;
            }
        });
        FabricModelPredicateProviderRegistry.register(ItemInit.GOLDEN_LEATHER_FLASK, new Identifier("empty"), (stack, world, entity, seed) -> {
            NbtCompound tags = stack.getNbt();
            if (entity == null || (stack.hasNbt() && tags.getInt("leather_flask") == 0)) {
                return 1.0F;
            } else {
                return 0.0F;
            }
        });
        FabricModelPredicateProviderRegistry.register(ItemInit.DIAMOND_LEATHER_FLASK, new Identifier("empty"), (stack, world, entity, seed) -> {
            NbtCompound tags = stack.getNbt();
            if (entity == null || (stack.hasNbt() && tags.getInt("leather_flask") == 0)) {
                return 1.0F;
            } else {
                return 0.0F;
            }
        });
        FabricModelPredicateProviderRegistry.register(ItemInit.NETHERITE_LEATHER_FLASK, new Identifier("empty"), (stack, world, entity, seed) -> {
            NbtCompound tags = stack.getNbt();
            if (entity == null || (stack.hasNbt() && tags.getInt("leather_flask") == 0)) {
                return 1.0F;
            } else {
                return 0.0F;
            }
        });
    }
}
