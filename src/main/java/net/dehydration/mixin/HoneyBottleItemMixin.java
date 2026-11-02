package net.dehydration.mixin;

import net.dehydration.init.ConfigInit;
import net.dehydration.misc.ThirstTooltipData;
import net.dehydration.util.ThirstHelper;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.HoneyBottleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;

@Mixin(HoneyBottleItem.class)
public abstract class HoneyBottleItemMixin extends Item {

    public HoneyBottleItemMixin(Properties settings) {
        super(settings);
    }

    // check here and for milk and potion
    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if (ConfigInit.CONFIG.thirst_preview) {
            int thirstQuench = ThirstHelper.getThirstQuench(stack, true);
            return Optional.of(new ThirstTooltipData(0, thirstQuench));
        }
        return super.getTooltipImage(stack);
    }
}
