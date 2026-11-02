package net.dehydration.mixin;

import net.dehydration.util.ThirstHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "finishUsingItem", at = @At("HEAD"))
    private void finishUsingMixin(Level world, LivingEntity user, CallbackInfoReturnable<ItemStack> info) {
        if (user instanceof ServerPlayer serverPlayerEntity) {
            ThirstHelper.hydratePlayer(serverPlayerEntity, (ItemStack) (Object) this);
        }
    }
}
