package net.dehydration.init;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.RenderLayer;

public class RenderInit {

    public static void init() {
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> BiomeColors.getWaterColor(world, pos), BlockInit.CAMPFIRE_CAULDRON_BLOCK);
        BlockRenderLayerMap.INSTANCE.putBlock(BlockInit.CAMPFIRE_CAULDRON_BLOCK, RenderLayer.getCutout());
    }

}
