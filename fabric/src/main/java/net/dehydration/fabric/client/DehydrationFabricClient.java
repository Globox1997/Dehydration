package net.dehydration.fabric.client;

import net.dehydration.block.render.BambooPumpRenderer;
import net.dehydration.init.BlockInit;
import net.dehydration.init.FluidInit;
import net.dehydration.init.ModelProviderInit;
import net.dehydration.init.RenderInit;
import net.dehydration.network.ThirstClientPacket;
import net.dehydration.network.packet.ExcludedThirstPacket;
import net.dehydration.network.packet.HydrationTemplatePacket;
import net.dehydration.network.packet.ThirstPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ClientTooltipComponentCallback;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;

@Environment(EnvType.CLIENT)
public class DehydrationFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModelProviderInit.register((id, codec) -> ConditionalItemModelProperties.ID_MAPPER.put(id, codec));

        RenderInit.registerBlockTints((block, tints) -> BlockColorRegistry.register(tints, block));
        BlockEntityRenderers.register(BlockInit.BAMBOO_PUMP_ENTITY, BambooPumpRenderer::new);
        FluidRenderingRegistry.register(FluidInit.PURIFIED_WATER, FluidInit.PURIFIED_FLOWING_WATER, RenderInit.PURIFIED_WATER_MODEL);
        FluidRenderingRegistry.setBlockTransparency(BlockInit.PURIFIED_WATER, true);
        ClientTooltipComponentCallback.EVENT.register(RenderInit::createTooltipComponent);

        ClientPlayNetworking.registerGlobalReceiver(ThirstPacket.PACKET_ID, (payload, context) -> context.client().execute(() -> ThirstClientPacket.handleThirst(payload)));
        ClientPlayNetworking.registerGlobalReceiver(ExcludedThirstPacket.PACKET_ID, (payload, context) -> context.client().execute(() -> ThirstClientPacket.handleExcludedThirst(payload)));
        ClientPlayNetworking.registerGlobalReceiver(HydrationTemplatePacket.PACKET_ID, (payload, context) -> context.client().execute(() -> ThirstClientPacket.handleHydrationTemplates(payload)));
    }

}
