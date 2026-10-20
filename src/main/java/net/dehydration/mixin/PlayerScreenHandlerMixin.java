package net.dehydration.mixin;

import net.dehydration.init.ItemInit;
import net.dehydration.item.LeatherFlask;
import net.dehydration.item.component.FlaskComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PlayerScreenHandler.class)
public class PlayerScreenHandlerMixin {

    @Inject(method = "quickMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;copy()Lnet/minecraft/item/ItemStack;"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onCraftedLeatherFlask(PlayerEntity player, int slot, CallbackInfoReturnable<ItemStack> cir, ItemStack itemStack, Slot slot2, ItemStack itemStack2) {
        if (player instanceof ServerPlayerEntity && slot2 instanceof CraftingResultSlot) {
            if (itemStack2.getItem() instanceof LeatherFlask && itemStack2.get(ItemInit.FLASK_DATA) == null) {
                itemStack2.set(ItemInit.FLASK_DATA, new FlaskComponent(0, 0));
            }
        }
    }
}
