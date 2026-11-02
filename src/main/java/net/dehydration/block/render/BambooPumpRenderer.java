package net.dehydration.block.render;

import org.jspecify.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.dehydration.block.entity.BambooPumpEntity;
import net.dehydration.init.BlockInit;
import net.dehydration.init.ItemInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public class BambooPumpRenderer implements BlockEntityRenderer<BambooPumpEntity, BambooPumpRenderer.BambooPumpRenderState> {

    private final ItemModelResolver itemModelResolver;

    public BambooPumpRenderer(BlockEntityRendererProvider.Context ctx) {
        this.itemModelResolver = ctx.itemModelResolver();
    }

    @Override
    public BambooPumpRenderState createRenderState() {
        return new BambooPumpRenderState();
    }

    @Override
    public void extractRenderState(BambooPumpEntity blockEntity, BambooPumpRenderState state, float tickDelta, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, tickDelta, cameraPosition, breakProgress);
        state.item.clear();
        state.facing = null;
        if (blockEntity.getLevel() != null && !blockEntity.isEmpty() && !blockEntity.getItem(0).is(Items.BUCKET) && !blockEntity.getItem(0).is(ItemInit.PURIFIED_BUCKET)) {
            BlockState blockState = blockEntity.getLevel().getBlockState(blockEntity.getBlockPos());
            if (!blockState.isAir() && blockState.is(BlockInit.BAMBOO_PUMP_BLOCK)) {
                state.facing = blockState.getValue(HorizontalDirectionalBlock.FACING);
                this.itemModelResolver.updateForTopItem(state.item, blockEntity.getItem(0), ItemDisplayContext.HEAD, blockEntity.getLevel(), null, (int) blockEntity.getBlockPos().asLong());
            }
        }
    }

    @Override
    public void submit(BambooPumpRenderState state, PoseStack matrices, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (state.facing == null || state.item.isEmpty()) {
            return;
        }
        Direction blockDirection = state.facing;
        matrices.pushPose();
        if (blockDirection == Direction.NORTH) {
            matrices.translate(0.5D, -0.05D, 0.38D);
            matrices.rotateDegrees(Axis.YP, 180F);
        } else if (blockDirection == Direction.EAST) {
            matrices.translate(0.62D, -0.05D, 0.5D);
            matrices.rotateDegrees(Axis.YP, 90F);
        } else if (blockDirection == Direction.SOUTH)
            matrices.translate(0.5D, -0.05D, 0.62D);
        else if (blockDirection == Direction.WEST) {
            matrices.translate(0.38D, -0.05D, 0.5D);
            matrices.rotateDegrees(Axis.YP, 270F);
        }
        matrices.scale(0.5F, 0.5F, 0.5F);

        state.item.submit(matrices, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        matrices.popPose();
    }

    @Environment(EnvType.CLIENT)
    public static class BambooPumpRenderState extends BlockEntityRenderState {
        public final ItemStackRenderState item = new ItemStackRenderState();
        @Nullable
        public Direction facing;
    }

}
