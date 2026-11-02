package net.dehydration.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.dehydration.init.ItemInit;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.item.Item;

@Mixin(CauldronInteraction.class)
public interface CauldronBehaviorMixin {

    @Inject(method = "addDefaultInteractions", at = @At("TAIL"))
    private static void registerBucketBehaviorMixin(Map<Item, CauldronInteraction> behavior, CallbackInfo info) {
        behavior.put(ItemInit.PURIFIED_BUCKET, CauldronInteraction.FILL_WATER);
    }
}
