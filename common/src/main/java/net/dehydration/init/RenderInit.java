package net.dehydration.init;

import net.dehydration.DehydrationMain;
import net.dehydration.block.CampfireCauldronBlock;
import net.dehydration.block.entity.CampfireCauldronEntity;
import net.dehydration.misc.ThirstTooltipComponent;
import net.dehydration.misc.ThirstTooltipData;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.util.ARGB;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;

public class RenderInit {

    public static final Identifier THIRST_ICON = DehydrationMain.identifierOf("textures/gui/thirst.png");
    public static final int PURIFIED_WATER_COLOR = ARGB.opaque(3708358);

    public static final BlockTintSource CAMPFIRE_CAULDRON_TINT = new BlockTintSource() {
        @Override
        public int color(BlockState state) {
            return PURIFIED_WATER_COLOR;
        }

        @Override
        public int colorInWorld(BlockState state, BlockAndTintGetter world, BlockPos pos) {
            return world.getBlockEntity(pos) != null ? isPurifiedWater(world, pos, state) ? PURIFIED_WATER_COLOR : BiomeColors.getAverageWaterColor(world, pos) : PURIFIED_WATER_COLOR;
        }
    };

    public static final FluidModel.Unbaked PURIFIED_WATER_MODEL = new FluidModel.Unbaked(new Material(Identifier.withDefaultNamespace("block/water_still")),
            new Material(Identifier.withDefaultNamespace("block/water_flow")), new Material(Identifier.withDefaultNamespace("block/water_overlay")), BlockTintSources.constant(PURIFIED_WATER_COLOR));

    public static void registerBlockTints(BiConsumer<Block, List<BlockTintSource>> registrar) {
        registrar.accept(BlockInit.CAMPFIRE_CAULDRON_BLOCK, List.of(CAMPFIRE_CAULDRON_TINT));
        registrar.accept(BlockInit.COPPER_WATER_CAULDRON_BLOCK, List.of(BlockTintSources.water()));
        registrar.accept(BlockInit.COPPER_PURIFIED_WATER_CAULDRON_BLOCK, List.of(BlockTintSources.constant(PURIFIED_WATER_COLOR)));
        registrar.accept(BlockInit.RAINWATER_WATER_COLLECTOR_BLOCK, List.of(BlockTintSources.water()));
        registrar.accept(BlockInit.RAINWATER_PURIFIED_WATER_COLLECTOR_BLOCK, List.of(BlockTintSources.constant(PURIFIED_WATER_COLOR)));
    }

    @Nullable
    public static ClientTooltipComponent createTooltipComponent(TooltipComponent data) {
        if (data instanceof ThirstTooltipData) {
            return new ThirstTooltipComponent((ThirstTooltipData) data);
        }
        return null;
    }

    private static boolean isPurifiedWater(BlockAndTintGetter blockRenderView, BlockPos blockPos, BlockState state) {
        if (blockRenderView.getBlockEntity(blockPos) != null && blockRenderView.getBlockEntity(blockPos) instanceof CampfireCauldronEntity) {
            return ((CampfireCauldronBlock) state.getBlock()).isPurifiedWater(blockRenderView.getBlockEntity(blockPos).getLevel(), blockPos);
        }
        return false;

    }

}
