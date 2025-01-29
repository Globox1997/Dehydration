package net.dehydration.mixin;

import net.dehydration.util.ThirstHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "finishUsing", at = @At("HEAD"))
    private void finishUsingMixin(World world, LivingEntity user, CallbackInfoReturnable<ItemStack> info) {
        if (user instanceof ServerPlayerEntity serverPlayerEntity) {
            ThirstHelper.hydratePlayer(serverPlayerEntity, (ItemStack) (Object) this);
        }
    }
}
