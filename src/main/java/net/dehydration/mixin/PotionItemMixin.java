package net.dehydration.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.dehydration.access.ThirstManagerAccess;
import net.dehydration.init.ConfigInit;
import net.dehydration.init.EffectInit;
import net.dehydration.misc.ThirstTooltipData;
import net.dehydration.thirst.ThirstManager;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.item.ThrowablePotionItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.world.World;

@Mixin(PotionItem.class)
public abstract class PotionItemMixin extends Item {

    public PotionItemMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "finishUsing", at = @At(value = "HEAD"))
    public void finishUsingMixin(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> info) {
        if (user instanceof PlayerEntity player) {
            Potion potion = PotionUtil.getPotion(stack);
            if (!world.isClient && this.isBadPotion(potion) && world.random.nextFloat() >= ConfigInit.CONFIG.potion_bad_thirst_chance) {
                player.addStatusEffect(new StatusEffectInstance(EffectInit.THIRST, ConfigInit.CONFIG.potion_bad_thirst_duration, 0, false, false, true));
            }
            ThirstManager thirstManager = ((ThirstManagerAccess) player).getThirstManager(player);
            thirstManager.add(ConfigInit.CONFIG.potion_thirst_quench);
        }
    }

    private boolean isBadPotion(Potion potion) {
        if (potion == Potions.WATER || potion == Potions.AWKWARD || potion == Potions.THICK || potion == Potions.HARMING || potion == Potions.LONG_POISON || potion == Potions.LONG_SLOWNESS
                || potion == Potions.LONG_WEAKNESS || potion == Potions.MUNDANE || potion == Potions.POISON || potion == Potions.SLOWNESS || potion == Potions.STRONG_HARMING
                || potion == Potions.STRONG_POISON || potion == Potions.STRONG_SLOWNESS || potion == Potions.WEAKNESS) {
            return true;
        } else
            return false;
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        if (!(stack.getItem() instanceof ThrowablePotionItem)) {
            if (isBadPotion(PotionUtil.getPotion(stack)))
                return Optional.of(new ThirstTooltipData(2, ConfigInit.CONFIG.potion_thirst_quench));

            return Optional.of(new ThirstTooltipData(0, ConfigInit.CONFIG.potion_thirst_quench));
        }
        return Optional.empty();
    }

}
