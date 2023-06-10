package net.dehydration.block.render;

import net.dehydration.block.entity.BambooPumpEntity;
import net.dehydration.init.ItemInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class BambooPumpRenderer implements BlockEntityRenderer<BambooPumpEntity> {

    public BambooPumpRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(BambooPumpEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (blockEntity.getWorld() != null && !blockEntity.isEmpty() && !blockEntity.getStack(0).isOf(Items.BUCKET) && !blockEntity.getStack(0).isOf(ItemInit.PURIFIED_BUCKET)) {
            BlockState blockState = blockEntity.getWorld().getBlockState(blockEntity.getPos());
            if (!blockState.isAir()) {
                Direction blockDirection = blockState.get(HorizontalFacingBlock.FACING);
                matrices.push();
                if (blockDirection == Direction.NORTH) {
                    matrices.translate(0.5D, -0.05D, 0.38D);
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((180F)));
                } else if (blockDirection == Direction.EAST) {
                    matrices.translate(0.62D, -0.05D, 0.5D);
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((90F)));
                } else if (blockDirection == Direction.SOUTH)
                    matrices.translate(0.5D, -0.05D, 0.62D);
                else if (blockDirection == Direction.WEST) {
                    matrices.translate(0.38D, -0.05D, 0.5D);
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((270F)));
                }
                matrices.scale(0.5F, 0.5F, 0.5F);

                MinecraftClient.getInstance().getItemRenderer().renderItem(blockEntity.getStack(0), ModelTransformationMode.HEAD, light, overlay, matrices, vertexConsumers, blockEntity.getWorld(),
                        (int) blockEntity.getPos().asLong());
                matrices.pop();
            }
        }
    }

}
