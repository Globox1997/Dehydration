package net.dehydration.mixin.client;

import com.mojang.authlib.GameProfile;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.dehydration.access.ThirstManagerAccess;
import net.dehydration.thirst.ThirstManager;

@Mixin(LocalPlayer.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayer {

    public ClientPlayerEntityMixin(ClientLevel world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;setSprinting(Z)V", shift = Shift.AFTER))
    private void tickMovementMixin(CallbackInfo info) {
        ThirstManager thirstManager = ((ThirstManagerAccess) this).getThirstManager();
        if (thirstManager.hasThirst() && !this.isCreative() && thirstManager.getThirstLevel() < 6) {
            this.setSprinting(false);
        }
    }

}
