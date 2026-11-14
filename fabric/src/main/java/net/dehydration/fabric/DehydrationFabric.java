package net.dehydration.fabric;

import java.util.function.BiConsumer;

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
import net.dehydration.network.packet.ExcludedThirstPacket;
import net.dehydration.network.packet.HydrationTemplatePacket;
import net.dehydration.network.packet.ThirstPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.ItemStack;

public class DehydrationFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        DehydrationMain.init();

        SoundInit.register(registrar(BuiltInRegistries.SOUND_EVENT));
        FluidInit.register(registrar(BuiltInRegistries.FLUID));
        EffectInit.register(registrar(BuiltInRegistries.MOB_EFFECT));
        ItemInit.registerComponents(registrar(BuiltInRegistries.DATA_COMPONENT_TYPE));
        BlockInit.registerBlocks(registrar(BuiltInRegistries.BLOCK));
        ItemInit.registerItems(registrar(BuiltInRegistries.ITEM));
        ItemInit.registerPotions(registrar(BuiltInRegistries.POTION));
        BlockInit.registerBlockEntityTypes(registrar(BuiltInRegistries.BLOCK_ENTITY_TYPE));
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ItemInit.DEHYDRATION_ITEM_GROUP, FabricCreativeModeTab.builder().icon(() -> new ItemStack(ItemInit.LEATHER_FLASK))
                .title(Component.translatable("item.dehydration.item_group")).displayItems((parameters, output) -> ItemInit.ITEM_GROUP_ITEMS.forEach(output::accept)).build());

        DehydrationMain.registerBehaviors();
        FabricFluidStorages.register();

        PayloadTypeRegistry.clientboundPlay().register(ThirstPacket.PACKET_ID, ThirstPacket.PACKET_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ExcludedThirstPacket.PACKET_ID, ExcludedThirstPacket.PACKET_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(HydrationTemplatePacket.PACKET_ID, HydrationTemplatePacket.PACKET_CODEC);

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> CommandInit.register(dispatcher));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> EventInit.onPlayerJoin(handler.player));
        ServerEntityLevelChangeEvents.AFTER_PLAYER_CHANGE_LEVEL.register((player, origin, destination) -> EventInit.onPlayerChangeLevel(player));
        ServerPlayerEvents.COPY_FROM.register(EventInit::onPlayerCopy);
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> EventInit.onPlayerRespawn(newPlayer));
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> EventInit.modifyLootTable(key, tableBuilder::pool));
        UseItemCallback.EVENT.register(EventInit::onUseItem);
        UseBlockCallback.EVENT.register(EventInit::onUseBlock);

        ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(DehydrationMain.identifierOf("loader"), JsonReaderInit.DATA_LOADER);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, serverResourceManager, success) -> JsonReaderInit.onDataPackReload(server, success));

        FabricLoader.getInstance().getEntrypointContainers("dehydration", DehydrationAPI.class).forEach((entrypoint) -> {
            String id = entrypoint.getProvider().getMetadata().getId();
            try {
                DehydrationMain.registerApi(id, entrypoint.getEntrypoint());
            } catch (Throwable exception) {
                DehydrationMain.LOGGER.error("Mod {} is providing a broken DehydrationAPI implementation", id, exception);
            }
        });
    }

    private static <V, T extends V> BiConsumer<Identifier, T> registrar(Registry<V> registry) {
        return (id, value) -> Registry.register(registry, id, value);
    }

}
