package net.dehydration.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.dehydration.init.ItemInit;
import net.minecraft.core.Holder;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;

@Mixin(PotionContents.class)
public class PotionContentsComponentMixin {

    @Shadow
    public Optional<Holder<Potion>> potion() {
        return null;
    };

    @SuppressWarnings("deprecation")
    @Inject(method = "getColor()I", at = @At("HEAD"), cancellable = true)
    private void getColorMixin(CallbackInfoReturnable<Integer> info) {
        if (this.potion() == null || !this.potion().isPresent() || this.potion().get() == null) {
            return;
        }
        if (this.potion().get().is(ItemInit.PURIFIED_WATER)) {
            info.setReturnValue(3708358);
        }
    }

}
