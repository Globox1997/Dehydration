package net.dehydration.init;

import net.dehydration.DehydrationMain;
import net.dehydration.block.CampfireCauldronBlock;
import net.dehydration.block.entity.CampfireCauldronEntity;
import net.dehydration.block.render.BambooPumpRenderer;
import net.dehydration.misc.ThirstTooltipComponent;
import net.dehydration.misc.ThirstTooltipData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

@Environment(EnvType.CLIENT)
public class RenderInit {

    public static final Identifier THIRST_ICON = DehydrationMain.identifierOf("textures/gui/thirst.png");

    public static void init() {
        ColorProviderRegistry.BLOCK.register(
                (state, world, pos, tintIndex) -> world != null && world.getBlockEntity(pos) != null ? isPurifiedWater(world, pos, state) ? 3708358 : BiomeColors.getWaterColor(world, pos) : 3708358,
                BlockInit.CAMPFIRE_CAULDRON_BLOCK);
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> BiomeColors.getWaterColor(world, pos), BlockInit.COPPER_WATER_CAULDRON_BLOCK);
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> 3708358, BlockInit.COPPER_PURIFIED_WATER_CAULDRON_BLOCK);
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> BiomeColors.getWaterColor(world, pos), BlockInit.RAINWATER_WATER_COLLECTOR_BLOCK);
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> 3708358, BlockInit.RAINWATER_PURIFIED_WATER_COLLECTOR_BLOCK);

        BlockRenderLayerMap.INSTANCE.putBlock(BlockInit.CAMPFIRE_CAULDRON_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockInit.COPPER_CAULDRON_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockInit.COPPER_WATER_CAULDRON_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockInit.COPPER_POWDERED_CAULDRON_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockInit.COPPER_PURIFIED_WATER_CAULDRON_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockInit.BAMBOO_PUMP_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockInit.RAINWATER_COLLECTOR_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockInit.RAINWATER_WATER_COLLECTOR_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockInit.RAINWATER_POWDERED_COLLECTOR_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockInit.RAINWATER_PURIFIED_WATER_COLLECTOR_BLOCK, RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), FluidInit.PURIFIED_WATER, FluidInit.PURIFIED_FLOWING_WATER);

        BlockEntityRendererFactories.register(BlockInit.BAMBOO_PUMP_ENTITY, BambooPumpRenderer::new);

        FluidRenderHandlerRegistry.INSTANCE.register(FluidInit.PURIFIED_WATER, SimpleFluidRenderHandler.coloredWater(3708358));
        FluidRenderHandlerRegistry.INSTANCE.register(FluidInit.PURIFIED_FLOWING_WATER, SimpleFluidRenderHandler.coloredWater(3708358));
        FluidRenderHandlerRegistry.INSTANCE.setBlockTransparency(BlockInit.PURIFIED_WATER, true);

        // Can not be used cause injection point is not working properly with chat rendering
        // HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
        //     MinecraftClient client = MinecraftClient.getInstance();
        //     ThirstHudRender.renderThirstHud(drawContext, client, client.player, drawContext.getScaledWindowWidth(), drawContext.getScaledWindowHeight(), client.inGameHud.getTicks());
        // });

        TooltipComponentCallback.EVENT.register((data) -> {
            if (data instanceof ThirstTooltipData) {
                return new ThirstTooltipComponent((ThirstTooltipData) data);
            }
            return null;
        });

    }

    private static boolean isPurifiedWater(BlockRenderView blockRenderView, BlockPos blockPos, BlockState state) {
        if (blockRenderView.getBlockEntity(blockPos) != null && blockRenderView.getBlockEntity(blockPos) instanceof CampfireCauldronEntity) {
            return ((CampfireCauldronBlock) state.getBlock()).isPurifiedWater(blockRenderView.getBlockEntity(blockPos).getWorld(), blockPos);
        }
        return false;

    }

}
