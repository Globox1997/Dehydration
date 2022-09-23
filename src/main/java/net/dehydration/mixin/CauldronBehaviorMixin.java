package net.dehydration.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.dehydration.init.ItemInit;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.item.Item;

@Mixin(CauldronBehavior.class)
public interface CauldronBehaviorMixin {

    @Inject(method = "registerBucketBehavior", at = @At("TAIL"))
    private static void registerBucketBehaviorMixin(Map<Item, CauldronBehavior> behavior, CallbackInfo info) {
        behavior.put(ItemInit.PURIFIED_BUCKET, CauldronBehavior.FILL_WITH_WATER);
    }
}
