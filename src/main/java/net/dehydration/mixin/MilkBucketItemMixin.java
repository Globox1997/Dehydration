package net.dehydration.mixin;

import net.dehydration.DehydrationMain;
import net.dehydration.init.ConfigInit;
import net.dehydration.misc.ThirstTooltipData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MilkBucketItem;
import net.minecraft.item.tooltip.TooltipData;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;

@Mixin(MilkBucketItem.class)
public abstract class MilkBucketItemMixin extends Item {

    public MilkBucketItemMixin(Settings settings) {
        super(settings);
    }

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
                thirstQuench = ConfigInit.CONFIG.milk_thirst_quench;
            }
            return Optional.of(new ThirstTooltipData(1, thirstQuench));
        } else {
            return super.getTooltipData(stack);
        }
    }

}
