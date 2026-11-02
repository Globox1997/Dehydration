package net.dehydration.mixin.client;

import net.dehydration.thirst.ThirstHudRender;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.fabricmc.api.EnvType;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Gui.class)
public abstract class InGameHudMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    private Player getCameraPlayer() {
        return null;
    }

    @ModifyReturnValue(method = "getVisibleVehicleHeartRows", at = @At("RETURN"))
    private int getHeartRowsMixin(int original) {
        return ThirstHudRender.shouldRenderThirstHud(this.getCameraPlayer()) ? original + 1 : original;
    }

    @Inject(method = "renderFood", at = @At("TAIL"))
    private void renderFoodMixin(GuiGraphics context, Player player, int top, int right, CallbackInfo info) {
        ThirstHudRender.renderThirstHud(context, this.minecraft, player, context.guiWidth(), context.guiHeight(), this.minecraft.gui.getGuiTicks());
    }

}