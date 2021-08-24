package net.dehydration.init;

import net.dehydration.block.CampfireCauldronBlock;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

public class RenderInit {

    public static void init() {
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> isPurifiedWater(world, pos, state) ? 3708358 : BiomeColors.getWaterColor(world, pos),
                BlockInit.CAMPFIRE_CAULDRON_BLOCK);
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> BiomeColors.getWaterColor(world, pos), BlockInit.COPPER_WATER_CAULDRON_BLOCK);
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> 3708358, BlockInit.COPPER_PURIFIED_WATER_CAULDRON_BLOCK);

        BlockRenderLayerMap.INSTANCE.putBlock(BlockInit.CAMPFIRE_CAULDRON_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockInit.COPPER_CAULDRON_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockInit.COPPER_WATER_CAULDRON_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockInit.COPPER_POWDERED_CAULDRON_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockInit.COPPER_PURIFIED_WATER_CAULDRON_BLOCK, RenderLayer.getCutout());
    }

    private static boolean isPurifiedWater(BlockRenderView blockRenderView, BlockPos blockPos, BlockState state) {
        if (blockRenderView.getBlockEntity(blockPos) != null) {
            return ((CampfireCauldronBlock) state.getBlock()).isPurifiedWater(blockRenderView.getBlockEntity(blockPos).getWorld(), blockPos);
        }
        return false;

    }

}
