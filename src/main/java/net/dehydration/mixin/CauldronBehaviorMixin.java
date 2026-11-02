package net.dehydration.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.dehydration.init.ItemInit;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.cauldron.CauldronInteractions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;

@Mixin(CauldronInteractions.class)
public class CauldronBehaviorMixin {

    @Inject(method = "addDefaultInteractions", at = @At("TAIL"))
    private static void registerBucketBehaviorMixin(CauldronInteraction.Dispatcher behavior, CallbackInfo info) {
        behavior.put(ItemInit.PURIFIED_BUCKET, (state, world, pos, player, hand, stack) -> CauldronInteractions.emptyBucket(world, pos, player, hand, stack,
                Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3), SoundEvents.BUCKET_EMPTY));
    }
}
