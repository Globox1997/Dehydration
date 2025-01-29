package net.dehydration.mixin;

import net.dehydration.DehydrationMain;
import net.dehydration.init.ConfigInit;
import net.dehydration.misc.ThirstTooltipData;
import net.minecraft.item.HoneyBottleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;

@Mixin(HoneyBottleItem.class)
public abstract class HoneyBottleItemMixin extends Item {

    public HoneyBottleItemMixin(Settings settings) {
        super(settings);
    }

    // check here and for milk and potion
    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        if (ConfigInit.CONFIG.thirst_preview) {
            int thirstQuench = 0;
            for (int i = 0; i < DehydrationMain.HYDRATION_TEMPLATES.size(); i++) {
                if (DehydrationMain.HYDRATION_TEMPLATES.get(i).containsItem(stack.getItem())) {
                    thirstQuench = DehydrationMain.HYDRATION_TEMPLATES.get(i).getHydration();
                    break;
                }
            }
            if (thirstQuench == 0) {
                thirstQuench = ConfigInit.CONFIG.honey_quench;
            }
            return Optional.of(new ThirstTooltipData(0, thirstQuench));
        }
        return super.getTooltipData(stack);
    }
}
