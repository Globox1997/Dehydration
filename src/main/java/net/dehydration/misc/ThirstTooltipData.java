package net.dehydration.misc;

import net.minecraft.client.item.TooltipData;

public class ThirstTooltipData implements TooltipData {

    private final int thirstQuench;
    private final int quality;

    public ThirstTooltipData(int quality, int thirstQuench) {
        this.thirstQuench = thirstQuench;
        this.quality = quality;
    }

    public int getThirstQuench() {
        return this.thirstQuench;
    }

    // quality: 0 = purified, 1 impurified, 2 dirty
    public int getDrinkQuality() {
        return this.quality;
    }

}
