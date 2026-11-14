package net.dehydration.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.dehydration.init.ConfigInit;
import net.dehydration.util.ThirstHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BottleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

@Mixin(BottleItem.class)
public class GlassBottleItemMixin {

    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V", ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void useMixin(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResult> info, List<AreaEffectCloud> list, ItemStack itemStack,
                          BlockHitResult blockHitResult, BlockPos blockPos) {
        if (!world.isClientSide() && ConfigInit.CONFIG.bottle_consumes_source_block) {
            ThirstHelper.drainWaterSource(user, world, blockPos);
        }
    }
}
