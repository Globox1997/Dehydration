package net.dehydration.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.PotionUtil;

@Mixin(PotionUtil.class)
public class PotionUtilMixin {

    @Inject(method = "Lnet/minecraft/potion/PotionUtil;getColor(Lnet/minecraft/item/ItemStack;)I", at = @At(value = "HEAD"), cancellable = true)
    private static void getColor(ItemStack stack, CallbackInfoReturnable<Integer> info) {
        NbtCompound compoundTag = stack.getNbt();
        if (compoundTag != null && compoundTag.asString().contains("purified_water")) {
            info.setReturnValue(3708358);
        }
    }

}
