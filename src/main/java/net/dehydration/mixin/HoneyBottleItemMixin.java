package net.dehydration.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.dehydration.DehydrationMain;
import net.dehydration.access.ThirstManagerAccess;
import net.dehydration.init.ConfigInit;
import net.dehydration.misc.ThirstTooltipData;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoneyBottleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

@Mixin(HoneyBottleItem.class)
public abstract class HoneyBottleItemMixin extends Item {

    public HoneyBottleItemMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "finishUsing", at = @At(value = "HEAD"))
    public void finishUsingMixin(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> info) {
        if (user instanceof PlayerEntity player) {
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
            ((ThirstManagerAccess) player).getThirstManager().add(thirstQuench);
        }
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
