package net.dehydration.init;

import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

public class ModelProviderInit {

  public static void init() {
    FabricModelPredicateProviderRegistry.register(ItemInit.LEATHER_FLASK, new Identifier("empty"),
        (stack, world, entity) -> {
          CompoundTag tags = stack.getTag();
          if (entity == null || (stack.hasTag() && tags.getInt("leather_flask") == 0)) {
            return 1.0F;
          } else {
            return 0.0F;
          }
        });
  }
}
