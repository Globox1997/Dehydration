package net.dehydration.mixin.client;

import net.dehydration.thirst.ThirstHudRender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private PlayerEntity getCameraPlayer() {
        return null;
    }

    @ModifyReturnValue(method = "getHeartRows", at = @At("RETURN"))
    private int getHeartRowsMixin(int original) {
        return ThirstHudRender.shouldRenderThirstHud(this.getCameraPlayer()) ? original + 1 : original;
    }

    @Inject(method = "renderFood", at = @At("TAIL"))
    private void renderFoodMixin(DrawContext context, PlayerEntity player, int top, int right, CallbackInfo info) {
        ThirstHudRender.renderThirstHud(context, this.client, player, context.getScaledWindowWidth(), context.getScaledWindowHeight(), this.client.inGameHud.getTicks());
    }

}