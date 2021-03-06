package net.dehydration;

import net.dehydration.init.ModelProviderInit;
import net.dehydration.init.RenderInit;
import net.dehydration.network.ThirstUpdateS2CPacket;
import net.fabricmc.api.ClientModInitializer;

public class DehydrationClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModelProviderInit.init();
        RenderInit.init();
        ThirstUpdateS2CPacket.init();
    }

}
