package net.dehydration.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.dehydration.block.entity.BambooPumpEntity;
import net.dehydration.init.BlockInit;
import net.dehydration.init.ItemInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

@Environment(EnvType.CLIENT)
public class BambooPumpRenderer implements BlockEntityRenderer<BambooPumpEntity> {

    public BambooPumpRenderer(BlockEntityRendererProvider.Context ctx) {
    }

    @Override
    public void render(BambooPumpEntity blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        if (blockEntity.getLevel() != null && !blockEntity.isEmpty() && !blockEntity.getItem(0).is(Items.BUCKET) && !blockEntity.getItem(0).is(ItemInit.PURIFIED_BUCKET)) {
            BlockState blockState = blockEntity.getLevel().getBlockState(blockEntity.getBlockPos());
            if (!blockState.isAir() && blockState.is(BlockInit.BAMBOO_PUMP_BLOCK)) {
                Direction blockDirection = blockState.getValue(HorizontalDirectionalBlock.FACING);
                matrices.pushPose();
                if (blockDirection == Direction.NORTH) {
                    matrices.translate(0.5D, -0.05D, 0.38D);
                    matrices.mulPose(Axis.YP.rotationDegrees((180F)));
                } else if (blockDirection == Direction.EAST) {
                    matrices.translate(0.62D, -0.05D, 0.5D);
                    matrices.mulPose(Axis.YP.rotationDegrees((90F)));
                } else if (blockDirection == Direction.SOUTH)
                    matrices.translate(0.5D, -0.05D, 0.62D);
                else if (blockDirection == Direction.WEST) {
                    matrices.translate(0.38D, -0.05D, 0.5D);
                    matrices.mulPose(Axis.YP.rotationDegrees((270F)));
                }
                matrices.scale(0.5F, 0.5F, 0.5F);

                Minecraft.getInstance().getItemRenderer().renderStatic(blockEntity.getItem(0), ItemDisplayContext.HEAD, light, overlay, matrices, vertexConsumers, blockEntity.getLevel(),
                        (int) blockEntity.getBlockPos().asLong());
                matrices.popPose();
            }
        }
    }

}
