package net.dehydration.init;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ModelProviderInit {

    public static void init() {
        ModelPredicateProviderRegistry.register(ItemInit.LEATHER_FLASK, new Identifier("empty"), (stack, world, entity, seed) -> {
            NbtCompound tags = stack.getNbt();
            if (entity == null || (stack.hasNbt() && tags.getInt("leather_flask") == 0)) {
                return 1.0F;
            } else {
                return 0.0F;
            }
        });
        ModelPredicateProviderRegistry.register(ItemInit.IRON_LEATHER_FLASK, new Identifier("empty"), (stack, world, entity, seed) -> {
            NbtCompound tags = stack.getNbt();
            if (entity == null || (stack.hasNbt() && tags.getInt("leather_flask") == 0)) {
                return 1.0F;
            } else {
                return 0.0F;
            }
        });
        ModelPredicateProviderRegistry.register(ItemInit.GOLDEN_LEATHER_FLASK, new Identifier("empty"), (stack, world, entity, seed) -> {
            NbtCompound tags = stack.getNbt();
            if (entity == null || (stack.hasNbt() && tags.getInt("leather_flask") == 0)) {
                return 1.0F;
            } else {
                return 0.0F;
            }
        });
        ModelPredicateProviderRegistry.register(ItemInit.DIAMOND_LEATHER_FLASK, new Identifier("empty"), (stack, world, entity, seed) -> {
            NbtCompound tags = stack.getNbt();
            if (entity == null || (stack.hasNbt() && tags.getInt("leather_flask") == 0)) {
                return 1.0F;
            } else {
                return 0.0F;
            }
        });
        ModelPredicateProviderRegistry.register(ItemInit.NETHERITE_LEATHER_FLASK, new Identifier("empty"), (stack, world, entity, seed) -> {
            NbtCompound tags = stack.getNbt();
            if (entity == null || (stack.hasNbt() && tags.getInt("leather_flask") == 0)) {
                return 1.0F;
            } else {
                return 0.0F;
            }
        });
    }
}
