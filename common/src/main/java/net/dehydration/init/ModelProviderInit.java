package net.dehydration.init;

import java.util.function.BiConsumer;

import com.mojang.serialization.MapCodec;

import net.dehydration.DehydrationMain;
import net.dehydration.misc.EmptyFlaskProperty;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.resources.Identifier;

public class ModelProviderInit {

    public static void register(BiConsumer<Identifier, MapCodec<? extends ConditionalItemModelProperty>> registrar) {
        registrar.accept(DehydrationMain.identifierOf("empty"), EmptyFlaskProperty.MAP_CODEC);
    }
}
