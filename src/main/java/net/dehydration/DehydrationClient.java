package net.dehydration;

import net.dehydration.init.ModelProviderInit;
import net.dehydration.init.RenderInit;
import net.dehydration.network.ThirstClientPacket;
import net.fabricmc.api.ClientModInitializer;

public class DehydrationClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModelProviderInit.init();
        RenderInit.init();
        ThirstClientPacket.init();
    }

}
