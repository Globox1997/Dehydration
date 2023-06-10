package net.dehydration.init;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class SoundInit {

    public static final Identifier FILL_FLASK = new Identifier("dehydration:fill_flask");
    public static final Identifier WATER_SIP = new Identifier("dehydration:water_sip");
    public static final Identifier EMPTY_FLASK = new Identifier("dehydration:empty_flask");
    public static final Identifier CAULDRON_BUBBLE = new Identifier("dehydration:cauldron_bubble");
    public static SoundEvent CAULDRON_BUBBLE_EVENT = SoundEvent.of(CAULDRON_BUBBLE);
    public static SoundEvent EMPTY_FLASK_EVENT = SoundEvent.of(EMPTY_FLASK);
    public static SoundEvent WATER_SIP_EVENT = SoundEvent.of(WATER_SIP);
    public static SoundEvent FILL_FLASK_EVENT = SoundEvent.of(FILL_FLASK);

    public static void init() {
        Registry.register(Registries.SOUND_EVENT, FILL_FLASK, FILL_FLASK_EVENT);
        Registry.register(Registries.SOUND_EVENT, WATER_SIP, WATER_SIP_EVENT);
        Registry.register(Registries.SOUND_EVENT, EMPTY_FLASK, EMPTY_FLASK_EVENT);
        Registry.register(Registries.SOUND_EVENT, CAULDRON_BUBBLE, CAULDRON_BUBBLE_EVENT);
    }
}
