package net.dehydration.init;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SoundInit {

  public static final Identifier FILL_FLASK = new Identifier("dehydration:fill_flask");
  public static SoundEvent FILL_FLASK_EVENT = new SoundEvent(FILL_FLASK);

  public static void init() {
    Registry.register(Registry.SOUND_EVENT, FILL_FLASK, FILL_FLASK_EVENT);
  }

}
