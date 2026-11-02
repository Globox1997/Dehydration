package net.dehydration.init;

import net.dehydration.DehydrationMain;
import net.dehydration.block.CampfireCauldronBlock;
import net.dehydration.block.entity.CampfireCauldronEntity;
import net.dehydration.block.render.BambooPumpRenderer;
import net.dehydration.misc.ThirstTooltipComponent;
import net.dehydration.misc.ThirstTooltipData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ClientTooltipComponentCallback;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.util.ARGB;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import java.util.List;

@Environment(EnvType.CLIENT)
public class RenderInit {

    public static final Identifier THIRST_ICON = DehydrationMain.identifierOf("textures/gui/thirst.png");
    private static final int PURIFIED_WATER_COLOR = ARGB.opaque(3708358);

    public static void init() {
        BlockColorRegistry.register(List.of(new BlockTintSource() {
            @Override
            public int color(BlockState state) {
                return PURIFIED_WATER_COLOR;
            }

            @Override
            public int colorInWorld(BlockState state, BlockAndTintGetter world, BlockPos pos) {
                return world.getBlockEntity(pos) != null ? isPurifiedWater(world, pos, state) ? PURIFIED_WATER_COLOR : BiomeColors.getAverageWaterColor(world, pos) : PURIFIED_WATER_COLOR;
            }
        }), BlockInit.CAMPFIRE_CAULDRON_BLOCK);
        BlockColorRegistry.register(List.of(BlockTintSources.water()), BlockInit.COPPER_WATER_CAULDRON_BLOCK);
        BlockColorRegistry.register(List.of(BlockTintSources.constant(PURIFIED_WATER_COLOR)), BlockInit.COPPER_PURIFIED_WATER_CAULDRON_BLOCK);
        BlockColorRegistry.register(List.of(BlockTintSources.water()), BlockInit.RAINWATER_WATER_COLLECTOR_BLOCK);
        BlockColorRegistry.register(List.of(BlockTintSources.constant(PURIFIED_WATER_COLOR)), BlockInit.RAINWATER_PURIFIED_WATER_COLLECTOR_BLOCK);

        BlockEntityRenderers.register(BlockInit.BAMBOO_PUMP_ENTITY, BambooPumpRenderer::new);

        FluidRenderingRegistry.register(FluidInit.PURIFIED_WATER, FluidInit.PURIFIED_FLOWING_WATER, new FluidModel.Unbaked(new Material(Identifier.withDefaultNamespace("block/water_still")),
                new Material(Identifier.withDefaultNamespace("block/water_flow")), new Material(Identifier.withDefaultNamespace("block/water_overlay")), BlockTintSources.constant(PURIFIED_WATER_COLOR)));
        FluidRenderingRegistry.setBlockTransparency(BlockInit.PURIFIED_WATER, true);

        // Can not be used cause injection point is not working properly with chat rendering
        // HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
        //     MinecraftClient client = MinecraftClient.getInstance();
        //     ThirstHudRender.renderThirstHud(drawContext, client, client.player, drawContext.getScaledWindowWidth(), drawContext.getScaledWindowHeight(), client.inGameHud.getTicks());
        // });

        ClientTooltipComponentCallback.EVENT.register((data) -> {
            if (data instanceof ThirstTooltipData) {
                return new ThirstTooltipComponent((ThirstTooltipData) data);
            }
            return null;
        });

    }

    private static boolean isPurifiedWater(BlockAndTintGetter blockRenderView, BlockPos blockPos, BlockState state) {
        if (blockRenderView.getBlockEntity(blockPos) != null && blockRenderView.getBlockEntity(blockPos) instanceof CampfireCauldronEntity) {
            return ((CampfireCauldronBlock) state.getBlock()).isPurifiedWater(blockRenderView.getBlockEntity(blockPos).getLevel(), blockPos);
        }
        return false;

    }

}
