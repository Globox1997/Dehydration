package net.dehydration.init;

import net.dehydration.DehydrationMain;
import net.dehydration.misc.EmptyFlaskProperty;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;

@Environment(EnvType.CLIENT)
public class ModelProviderInit {

    public static void init() {
        ConditionalItemModelProperties.ID_MAPPER.put(DehydrationMain.identifierOf("empty"), EmptyFlaskProperty.MAP_CODEC);
    }
}
