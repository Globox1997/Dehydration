package net.dehydration.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.dehydration.access.ThristManagerAccess;
import net.dehydration.init.ConfigInit;
import net.dehydration.init.TagInit;
import net.dehydration.thirst.ThirstManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MushroomStewItem;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.world.World;

@Mixin(Item.class)
public class ItemMixin {

  @Inject(method = "finishUsing", at = @At(value = "HEAD"))
  public void finishUsing(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> info) {
    if (user instanceof PlayerEntity) {
      PlayerEntity player = (PlayerEntity) user;
      ThirstManager thirstManager = ((ThristManagerAccess) player).getThirstManager(player);
      int thirst = 0;
      if (stack.getItem() instanceof MushroomStewItem || stack.getItem() instanceof SuspiciousStewItem) {
        thirst = ConfigInit.CONFIG.stew_thirst_quench;
      }
      if (stack.getItem().isIn(TagInit.HYDRATING_FOOD)) {
        thirst = ConfigInit.CONFIG.food_thirst_quench;
      }
      thirstManager.add(thirst);
    }
  }

}
