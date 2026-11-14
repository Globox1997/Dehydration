package net.dehydration.mixin;

import net.dehydration.init.ItemInit;
import net.dehydration.item.LeatherFlask;
import net.dehydration.item.component.FlaskComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(InventoryMenu.class)
public class PlayerScreenHandlerMixin {

    @Inject(method = "quickMoveStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;copy()Lnet/minecraft/world/item/ItemStack;"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onCraftedLeatherFlask(Player player, int slot, CallbackInfoReturnable<ItemStack> cir, ItemStack itemStack, Slot slot2, ItemStack itemStack2) {
        if (player instanceof ServerPlayer && slot2 instanceof ResultSlot) {
            if (itemStack2.getItem() instanceof LeatherFlask && itemStack2.get(ItemInit.FLASK_DATA) == null) {
                itemStack2.set(ItemInit.FLASK_DATA, new FlaskComponent(0, 0));
            }
        }
    }
}
