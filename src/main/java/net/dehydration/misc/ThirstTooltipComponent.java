package net.dehydration.misc;

import com.mojang.blaze3d.systems.RenderSystem;

import net.dehydration.init.RenderInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class ThirstTooltipComponent implements TooltipComponent {

    private final int thirstQuench;
    private final int quality;

    public ThirstTooltipComponent(ThirstTooltipData data) {
        this.thirstQuench = data.getThirstQuench();
        this.quality = data.getDrinkQuality();
    }

    @Override
    public int getHeight() {
        return 11;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return this.thirstQuench * 9 / 2 + (this.thirstQuench % 2 != 0 ? 9 : 0);
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer, int z) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, RenderInit.THIRST_ICON);
        for (int i = 0; i < this.thirstQuench / 2; i++) {
            DrawableHelper.drawTexture(matrices, x + i * 9 - 1, y, 0, 0, 9, 9, 256, 256); // Background
            DrawableHelper.drawTexture(matrices, x + i * 9 - 1, y, this.quality * 18, 9, 9, 9, 256, 256);
        }
        if (this.thirstQuench % 2 != 0) {
            DrawableHelper.drawTexture(matrices, x + this.thirstQuench / 2 * 9 - 1, y, 0, 0, 9, 9, 256, 256); // Background
            DrawableHelper.drawTexture(matrices, x + this.thirstQuench / 2 * 9 - 1, y, this.quality * 18 + 9, 9, 9, 9, 256, 256);
        }
    }

}
