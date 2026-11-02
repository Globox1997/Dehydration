package net.dehydration.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record FlaskComponent(Integer fillLevel, Integer qualityLevel) {
    public static final FlaskComponent DEFAULT = new FlaskComponent(0, 0);

    public static final Codec<FlaskComponent> CODEC = RecordCodecBuilder
            .create(instance -> instance.group(Codec.INT.fieldOf("fill_level").forGetter(FlaskComponent::fillLevel), Codec.INT.fieldOf("quality_level").forGetter(FlaskComponent::qualityLevel))
                    .apply(instance, FlaskComponent::new));

    public static final StreamCodec<ByteBuf, FlaskComponent> PACKET_CODEC = StreamCodec.composite(ByteBufCodecs.INT, FlaskComponent::fillLevel, ByteBufCodecs.INT, FlaskComponent::qualityLevel,
            FlaskComponent::new);

}
