package net.dehydration.neoforge;

import java.util.ServiceLoader;

import net.dehydration.DehydrationMain;
import net.dehydration.api.DehydrationAPI;
import net.dehydration.init.BlockInit;
import net.dehydration.init.CommandInit;
import net.dehydration.init.EffectInit;
import net.dehydration.init.EventInit;
import net.dehydration.init.FluidInit;
import net.dehydration.init.ItemInit;
import net.dehydration.init.JsonReaderInit;
import net.dehydration.init.SoundInit;
import net.dehydration.neoforge.fluid.NeoForgePurifiedWaterFluid;
import net.dehydration.neoforge.transfer.NeoForgeCapabilities;
import net.dehydration.network.packet.ExcludedThirstPacket;
import net.dehydration.network.packet.HydrationTemplatePacket;
import net.dehydration.network.packet.ThirstPacket;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(DehydrationMain.MOD_ID)
public class DehydrationNeoForge {

    public DehydrationNeoForge(IEventBus modBus) {
        DehydrationMain.init();
        modBus.addListener(RegisterEvent.class, DehydrationNeoForge::register);
        modBus.addListener(RegisterPayloadHandlersEvent.class, DehydrationNeoForge::registerPayloads);
        modBus.addListener(RegisterCapabilitiesEvent.class, NeoForgeCapabilities::register);
        modBus.addListener(FMLCommonSetupEvent.class, event -> event.enqueueWork(DehydrationNeoForge::commonSetup));

        IEventBus eventBus = NeoForge.EVENT_BUS;
        eventBus.addListener(PlayerEvent.PlayerLoggedInEvent.class, event -> {
            if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                EventInit.onPlayerJoin(serverPlayer);
            }
        });
        eventBus.addListener(PlayerEvent.PlayerChangedDimensionEvent.class, event -> {
            if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                EventInit.onPlayerChangeLevel(serverPlayer);
            }
        });
        eventBus.addListener(PlayerEvent.Clone.class, event -> {
            if (event.getOriginal() instanceof ServerPlayer oldPlayer && event.getEntity() instanceof ServerPlayer newPlayer) {
                EventInit.onPlayerCopy(oldPlayer, newPlayer, !event.isWasDeath());
            }
        });
        eventBus.addListener(PlayerEvent.PlayerRespawnEvent.class, event -> {
            if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                EventInit.onPlayerRespawn(serverPlayer);
            }
        });
        eventBus.addListener(LootTableLoadEvent.class, event -> EventInit.modifyLootTable(event.getKey(), event.getTable()::addPool));
        eventBus.addListener(PlayerInteractEvent.RightClickItem.class, event -> {
            InteractionResult result = EventInit.onUseItem(event.getEntity(), event.getLevel(), event.getHand());
            if (result != InteractionResult.PASS) {
                event.setCancellationResult(result);
                event.setCanceled(true);
            }
        });
        eventBus.addListener(PlayerInteractEvent.RightClickBlock.class, event -> {
            InteractionResult result = EventInit.onUseBlock(event.getEntity(), event.getLevel(), event.getHand(), event.getHitVec());
            if (result != InteractionResult.PASS) {
                event.setCancellationResult(result);
                event.setCanceled(true);
            }
        });
        eventBus.addListener(RegisterCommandsEvent.class, event -> CommandInit.register(event.getDispatcher()));
        eventBus.addListener(AddServerReloadListenersEvent.class, event -> event.addListener(DehydrationMain.identifierOf("loader"), JsonReaderInit.DATA_LOADER));
        eventBus.addListener(OnDatapackSyncEvent.class, event -> {
            if (event.getPlayer() == null) {
                JsonReaderInit.onDataPackReload(event.getPlayerList().getServer(), true);
            }
        });
    }

    private static void register(RegisterEvent event) {
        event.register(Registries.SOUND_EVENT, helper -> SoundInit.register(helper::register));
        event.register(Registries.FLUID, helper -> FluidInit.register(helper::register));
        event.register(Registries.MOB_EFFECT, helper -> EffectInit.register(helper::register));
        event.register(Registries.BLOCK, helper -> BlockInit.registerBlocks(helper::register));
        event.register(Registries.ITEM, helper -> ItemInit.registerItems(helper::register));
        event.register(Registries.POTION, helper -> ItemInit.registerPotions(helper::register));
        event.register(Registries.BLOCK_ENTITY_TYPE, helper -> BlockInit.registerBlockEntityTypes(helper::register));
        event.register(Registries.DATA_COMPONENT_TYPE, helper -> ItemInit.registerComponents(helper::register));
        event.register(Registries.CREATIVE_MODE_TAB, helper -> helper.register(ItemInit.DEHYDRATION_ITEM_GROUP, CreativeModeTab.builder().icon(() -> new ItemStack(ItemInit.LEATHER_FLASK))
                .title(Component.translatable("item.dehydration.item_group")).displayItems((parameters, output) -> ItemInit.ITEM_GROUP_ITEMS.forEach(output::accept)).build()));
        event.register(NeoForgeRegistries.Keys.FLUID_TYPES, helper -> helper.register(DehydrationMain.identifierOf("purified_water"), NeoForgePurifiedWaterFluid.TYPE));
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(ThirstPacket.PACKET_ID, ThirstPacket.PACKET_CODEC);
        registrar.playToClient(ExcludedThirstPacket.PACKET_ID, ExcludedThirstPacket.PACKET_CODEC);
        registrar.playToClient(HydrationTemplatePacket.PACKET_ID, HydrationTemplatePacket.PACKET_CODEC);
    }

    private static void commonSetup() {
        DehydrationMain.registerBehaviors();
        ServiceLoader.load(DehydrationAPI.class, DehydrationNeoForge.class.getClassLoader()).stream().forEach(provider -> {
            String id = provider.type().getName();
            try {
                DehydrationMain.registerApi(id, provider.get());
            } catch (Throwable exception) {
                DehydrationMain.LOGGER.error("Mod {} is providing a broken DehydrationAPI implementation", id, exception);
            }
        });
    }

}
