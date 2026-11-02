package net.dehydration.misc;

import org.jspecify.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import net.dehydration.init.ItemInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public record EmptyFlaskProperty() implements ConditionalItemModelProperty {

    public static final MapCodec<EmptyFlaskProperty> MAP_CODEC = MapCodec.unit(new EmptyFlaskProperty());

    @Override
    public boolean get(ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
        return stack.get(ItemInit.FLASK_DATA) != null && stack.get(ItemInit.FLASK_DATA).fillLevel() <= 0;
    }

    @Override
    public MapCodec<EmptyFlaskProperty> type() {
        return MAP_CODEC;
    }
}
