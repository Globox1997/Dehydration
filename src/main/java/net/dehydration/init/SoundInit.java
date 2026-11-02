package net.dehydration.init;

import net.dehydration.DehydrationMain;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class SoundInit {

    public static final Identifier FILL_FLASK = DehydrationMain.identifierOf("fill_flask");
    public static final Identifier WATER_SIP = DehydrationMain.identifierOf("water_sip");
    public static final Identifier EMPTY_FLASK = DehydrationMain.identifierOf("empty_flask");
    public static final Identifier CAULDRON_BUBBLE = DehydrationMain.identifierOf("cauldron_bubble");
    public static SoundEvent CAULDRON_BUBBLE_EVENT = SoundEvent.createVariableRangeEvent(CAULDRON_BUBBLE);
    public static SoundEvent EMPTY_FLASK_EVENT = SoundEvent.createVariableRangeEvent(EMPTY_FLASK);
    public static SoundEvent WATER_SIP_EVENT = SoundEvent.createVariableRangeEvent(WATER_SIP);
    public static SoundEvent FILL_FLASK_EVENT = SoundEvent.createVariableRangeEvent(FILL_FLASK);

    public static void init() {
        Registry.register(BuiltInRegistries.SOUND_EVENT, FILL_FLASK, FILL_FLASK_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, WATER_SIP, WATER_SIP_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, EMPTY_FLASK, EMPTY_FLASK_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, CAULDRON_BUBBLE, CAULDRON_BUBBLE_EVENT);
    }
}
