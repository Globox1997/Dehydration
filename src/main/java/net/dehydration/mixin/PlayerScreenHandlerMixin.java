package net.dehydration.mixin;

import net.dehydration.item.LeatherFlask;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
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
    public void onCraftedLeatherFlask(PlayerEntity player, int slot, CallbackInfoReturnable<ItemStack> cir, ItemStack itemStack, Slot slot2, ItemStack itemStack2) {
        if (player instanceof ServerPlayerEntity && slot2 instanceof CraftingResultSlot) {
            if (itemStack2.getItem() instanceof LeatherFlask && !itemStack2.hasNbt()) {
                NbtCompound tags = new NbtCompound();
                tags.putInt("leather_flask", 0);
                itemStack2.setNbt(tags);
            }
        }
    }
}
