package net.dehydration.init;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class ModelProviderInit {

    public static void init() {
        for (int i = 0; i < ItemInit.FLASK_ITEM_LIST.size(); i++) {
            ItemProperties.register(ItemInit.FLASK_ITEM_LIST.get(i), ResourceLocation.parse("empty"), (stack, world, entity, seed) -> {
                if (stack.get(ItemInit.FLASK_DATA) == null || stack.get(ItemInit.FLASK_DATA).fillLevel() > 0) {
                    return 0.0F;
                } else {
                    return 1.0F;
                }
            });
        }
    }
}
