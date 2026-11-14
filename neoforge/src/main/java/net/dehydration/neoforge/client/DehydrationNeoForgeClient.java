package net.dehydration.neoforge.client;

import me.shedaniel.autoconfig.AutoConfigClient;
import net.dehydration.DehydrationMain;
import net.dehydration.block.render.BambooPumpRenderer;
import net.dehydration.config.DehydrationConfig;
import net.dehydration.init.BlockInit;
import net.dehydration.init.FluidInit;
import net.dehydration.init.ModelProviderInit;
import net.dehydration.init.RenderInit;
import net.dehydration.misc.ThirstTooltipComponent;
import net.dehydration.misc.ThirstTooltipData;
import net.dehydration.neoforge.fluid.NeoForgePurifiedWaterFluid;
import net.dehydration.network.ThirstClientPacket;
import net.dehydration.network.packet.ExcludedThirstPacket;
import net.dehydration.network.packet.HydrationTemplatePacket;
import net.dehydration.network.packet.ThirstPacket;
import net.dehydration.thirst.ThirstHudRender;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterConditionalItemModelPropertyEvent;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;

@Mod(value = DehydrationMain.MOD_ID, dist = Dist.CLIENT)
public class DehydrationNeoForgeClient {

    private static final Identifier UNDERWATER_LOCATION = Identifier.withDefaultNamespace("textures/misc/underwater.png");

    public DehydrationNeoForgeClient(IEventBus modBus, ModContainer container) {
        modBus.addListener(RegisterConditionalItemModelPropertyEvent.class, event -> ModelProviderInit.register(event::register));
        modBus.addListener(RegisterColorHandlersEvent.BlockTintSources.class, event -> RenderInit.registerBlockTints((block, tints) -> event.register(tints, block)));
        modBus.addListener(EntityRenderersEvent.RegisterRenderers.class, event -> event.registerBlockEntityRenderer(BlockInit.BAMBOO_PUMP_ENTITY, BambooPumpRenderer::new));
        modBus.addListener(RegisterFluidModelsEvent.class, event -> {
            event.register(RenderInit.PURIFIED_WATER_MODEL, FluidInit.PURIFIED_WATER);
            event.register(RenderInit.PURIFIED_WATER_MODEL, FluidInit.PURIFIED_FLOWING_WATER);
        });
        modBus.addListener(RegisterClientExtensionsEvent.class, event -> event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public Identifier getRenderOverlayTexture(Minecraft minecraft) {
                return UNDERWATER_LOCATION;
            }
        }, NeoForgePurifiedWaterFluid.TYPE));
        modBus.addListener(RegisterGuiLayersEvent.class, event -> event.registerAbove(VanillaGuiLayers.FOOD_LEVEL, DehydrationMain.identifierOf("thirst_level"), (graphics, deltaTracker) -> {
            Minecraft client = Minecraft.getInstance();
            if (client.gameMode != null && client.gameMode.canHurtPlayer() && client.getCameraEntity() instanceof Player player && ThirstHudRender.shouldRenderThirstHud(player)) {
                client.gui.hud.rightHeight += 10;
            }
        }));
        modBus.addListener(RegisterClientTooltipComponentFactoriesEvent.class, event -> event.register(ThirstTooltipData.class, ThirstTooltipComponent::new));
        modBus.addListener(RegisterClientPayloadHandlersEvent.class, event -> {
            event.register(ThirstPacket.PACKET_ID, (payload, context) -> ThirstClientPacket.handleThirst(payload));
            event.register(ExcludedThirstPacket.PACKET_ID, (payload, context) -> ThirstClientPacket.handleExcludedThirst(payload));
            event.register(HydrationTemplatePacket.PACKET_ID, (payload, context) -> ThirstClientPacket.handleHydrationTemplates(payload));
        });

        container.registerExtensionPoint(IConfigScreenFactory.class, (modContainer, parent) -> AutoConfigClient.getConfigScreen(DehydrationConfig.class, parent).get());
    }

}
