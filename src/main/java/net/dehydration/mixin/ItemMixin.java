package net.dehydration.mixin;

import net.dehydration.DehydrationMain;
import net.dehydration.init.ConfigInit;
import net.dehydration.init.TagInit;
import net.dehydration.misc.ThirstTooltipData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(method = "getTooltipData", at = @At("HEAD"), cancellable = true)
    private void getTooltipDataMixin(ItemStack stack, CallbackInfoReturnable<Optional<TooltipData>> info) {
        if (ConfigInit.CONFIG.thirst_preview) {
            int thirstQuench = 0;
            if (stack.isIn(TagInit.HYDRATING_STEW)) {
                thirstQuench = ConfigInit.CONFIG.stew_thirst_quench;
            } else if (stack.isIn(TagInit.HYDRATING_FOOD)) {
                thirstQuench = ConfigInit.CONFIG.food_thirst_quench;
            } else if (stack.isIn(TagInit.HYDRATING_DRINKS)) {
                thirstQuench = ConfigInit.CONFIG.drinks_thirst_quench;
            } else if (stack.isIn(TagInit.STRONGER_HYDRATING_STEW)) {
                thirstQuench = ConfigInit.CONFIG.stronger_stew_thirst_quench;
            } else if (stack.isIn(TagInit.STRONGER_HYDRATING_FOOD)) {
                thirstQuench = ConfigInit.CONFIG.stronger_food_thirst_quench;
            } else if (stack.isIn(TagInit.STRONGER_HYDRATING_DRINKS)) {
                thirstQuench = ConfigInit.CONFIG.stronger_drinks_thirst_quench;
            }
            for (int i = 0; i < DehydrationMain.HYDRATION_TEMPLATES.size(); i++) {
                if (DehydrationMain.HYDRATION_TEMPLATES.get(i).containsItem(stack.getItem())) {
                    thirstQuench = DehydrationMain.HYDRATION_TEMPLATES.get(i).getHydration();
                    break;
                }
            }

            if (thirstQuench > 0) {
                info.setReturnValue(Optional.of(new ThirstTooltipData(0, thirstQuench)));
            }
        }
    }

}
