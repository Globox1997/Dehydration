package net.dehydration.misc;

import net.dehydration.init.RenderInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;

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
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        for (int i = 0; i < this.thirstQuench / 2; i++) {
            context.drawTexture(RenderInit.THIRST_ICON, x + i * 9 - 1, y, 0, 0, 9, 9, 256, 256); // Background
            context.drawTexture(RenderInit.THIRST_ICON, x + i * 9 - 1, y, this.quality * 18, 9, 9, 9, 256, 256);
        }
        if (this.thirstQuench % 2 != 0) {
            context.drawTexture(RenderInit.THIRST_ICON, x + this.thirstQuench / 2 * 9 - 1, y, 0, 0, 9, 9, 256, 256); // Background
            context.drawTexture(RenderInit.THIRST_ICON, x + this.thirstQuench / 2 * 9 - 1, y, this.quality * 18 + 9, 9, 9, 9, 256, 256);
        }
    }

}
