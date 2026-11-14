package net.dehydration.init;

import java.util.function.BiConsumer;

import net.dehydration.DehydrationMain;
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

    public static void register(BiConsumer<Identifier, SoundEvent> registrar) {
        registrar.accept(FILL_FLASK, FILL_FLASK_EVENT);
        registrar.accept(WATER_SIP, WATER_SIP_EVENT);
        registrar.accept(EMPTY_FLASK, EMPTY_FLASK_EVENT);
        registrar.accept(CAULDRON_BUBBLE, CAULDRON_BUBBLE_EVENT);
    }

}
