package net.dehydration.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.dehydration.access.ThristManagerAccess;
import net.dehydration.init.ConfigInit;
import net.dehydration.init.EffectInit;
import net.dehydration.thirst.ThirstManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MilkBucketItem;
import net.minecraft.world.World;

@Mixin(MilkBucketItem.class)
public class MilkBucketItemMixin {

  @Inject(method = "finishUsing", at = @At(value = "HEAD"))
  public void finishUsingMixin(ItemStack stack, World world, LivingEntity user,
      CallbackInfoReturnable<ItemStack> info) {
    if (user instanceof PlayerEntity) {
      PlayerEntity player = (PlayerEntity) user;
      ThirstManager thirstManager = ((ThristManagerAccess) player).getThirstManager(player);
      thirstManager.add(ConfigInit.CONFIG.milk_thirst_quench);
      if (world.random.nextFloat() >= ConfigInit.CONFIG.milk_thirst_chance) {
        player.addStatusEffect(new StatusEffectInstance(EffectInit.THIRST, 300, 0, false, false, true));
      }
    }
  }

}
