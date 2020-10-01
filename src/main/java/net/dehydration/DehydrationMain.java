package net.dehydration;

import net.dehydration.init.*;
import net.fabricmc.api.ModInitializer;

public class DehydrationMain implements ModInitializer {

  @Override
  public void onInitialize() {
    ConfigInit.init();
    EffectInit.init();
    TagInit.init();
  }

}
