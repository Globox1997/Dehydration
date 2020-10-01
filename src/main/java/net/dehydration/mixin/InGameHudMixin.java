package net.dehydration.mixin;

import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.fabricmc.api.Environment;
import net.dehydration.access.ThristManagerAccess;
import net.dehydration.effect.DehydrationEffect;
import net.dehydration.init.ConfigInit;
import net.dehydration.thirst.ThirstManager;
import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper {
  @Shadow
  @Final
  @Mutable
  private final MinecraftClient client;
  @Shadow
  private int ticks;
  @Shadow
  private int scaledWidth;
  @Shadow
  private int scaledHeight;

  private float smoothRendering;
  private static final Identifier HEATING_ICON = new Identifier("dehydration:textures/misc/dehydration.png");
  private static final Identifier THIRST_ICON = new Identifier("dehydration:textures/misc/thirst.png");

  public InGameHudMixin(MinecraftClient client) {
    this.client = client;
  }

  @Inject(method = "Lnet/minecraft/client/gui/hud/InGameHud;render(Lnet/minecraft/client/util/math/MatrixStack;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbar(FLnet/minecraft/client/util/math/MatrixStack;)V"))
  private void renderHeatingIcon(MatrixStack matrixStack, float f, CallbackInfo info) {
    PlayerEntity playerEntity = client.player;
    if (!playerEntity.isCreative()) {
      if (playerEntity.world.getBiome(playerEntity.getBlockPos()).getTemperature() >= 2.0F
          && DehydrationEffect.wearsArmor(playerEntity) && playerEntity.world.isSkyVisible(playerEntity.getBlockPos())
          && playerEntity.world.isDay()) {
        if (smoothRendering < 0.995F) {
          smoothRendering = smoothRendering + 0.005F;
        }
        this.renderHeatingIconOverlay(matrixStack, smoothRendering);
      } else if (smoothRendering > 0.0F) {
        this.renderHeatingIconOverlay(matrixStack, smoothRendering);
        smoothRendering = smoothRendering - 0.01F;
      }

    }
  }

  private void renderHeatingIconOverlay(MatrixStack matrixStack, float smooth) {
    this.client.getTextureManager().bindTexture(HEATING_ICON);
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, smooth);
    DrawableHelper.drawTexture(matrixStack, (scaledWidth / 2) - ConfigInit.CONFIG.heat_icon_x,
        scaledHeight - ConfigInit.CONFIG.heat_icon_y, 0.0F, 0.0F, 10, 10, 10, 10);
  }

  @Inject(method = "renderStatusBars", at = @At(value = "TAIL"))
  private void renderStatusBarsMixin(MatrixStack matrices, CallbackInfo info) {
    PlayerEntity playerEntity = this.getCameraPlayer();
    if (playerEntity != null) {
      ThirstManager thirstManager = ((ThristManagerAccess) playerEntity).getThirstManager(playerEntity);
      int thirst = thirstManager.getThirstLevel();
      LivingEntity livingEntity = this.getRiddenEntity();
      int variable_one;
      int variable_two;
      int variable_three;
      int height = this.scaledHeight - 49;
      int width = this.scaledWidth / 2 + 91;
      if (this.getHeartCount(livingEntity) == 0) {
        for (variable_one = 0; variable_one < 10; ++variable_one) {
          variable_three = height;
          if (this.ticks % (thirst * 3 + 1) == 0) {
            variable_three = height + (client.world.random.nextInt(3) - 1); // bouncy
          }
          variable_two = width - variable_one * 8 - 9;
          this.client.getTextureManager().bindTexture(THIRST_ICON);
          if (variable_one * 2 + 1 < thirst) {
            this.drawTexture(matrices, variable_two, variable_three, 0, 0, 9, 9); // Big icon
          }
          if (variable_one * 2 + 1 == thirst) {
            this.drawTexture(matrices, variable_two, variable_three, 9, 0, 9, 9); // Small icon
          }
        }
      }
    }
  }

  // @Inject(method =
  // "Lnet/minecraft/client/gui/hud/InGameHud;render(Lnet/minecraft/client/util/math/MatrixStack;F)V",
  // at = @At(value = "INVOKE", target =
  // "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F"))
  // private void renderMixin(MatrixStack matrices, float tickDelta, CallbackInfo
  // info) {
  // if (this.client.player.hasStatusEffect(EffectInit.DEHYDRATION)) {
  // this.renderPortalOverlay(0.2F);
  // }
  // }

  @Shadow
  private PlayerEntity getCameraPlayer() {
    return null;
  }

  @Shadow
  private LivingEntity getRiddenEntity() {
    return null;
  }

  @Shadow
  private int getHeartCount(LivingEntity entity) {
    return 0;
  }

  // @Shadow
  // private void renderPortalOverlay(float f) {
  // }

  // @Inject(method =
  // "Lnet/minecraft/client/gui/hud/InGameHud;render(Lnet/minecraft/client/util/math/MatrixStack;F)V",
  // at = @At(value = "INVOKE", target =
  // "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbar(FLnet/minecraft/client/util/math/MatrixStack;)V"))
  // private void renderFreezingIcon(MatrixStack matrixStack, float f,
  // CallbackInfo info) {
  // PlayerEntity playerEntity = client.player;
  // if (!playerEntity.isCreative()) {
  // if (playerEntity.world.getBiome(playerEntity.getBlockPos()).getTemperature()
  // <= 0.0F
  // && !ColdEffect.hasWarmClothing(playerEntity) &&
  // !ColdEffect.isWarmBlockNearBy(playerEntity)) {
  // if (smoothRendering < 0.995F) {
  // smoothRendering = smoothRendering + 0.0025F;
  // }
  // this.renderHeatingIconOverlay(matrixStack, smoothRendering);
  // } else if (smoothRendering > 0.0F) {
  // this.renderHeatingIconOverlay(matrixStack, smoothRendering);
  // smoothRendering = smoothRendering - 0.01F;
  // }

  // }
  // }

  // private void renderHeatingIconOverlay(MatrixStack matrixStack, float smooth)
  // {
  // RenderSystem.enableBlend();
  // int scaledWidth = this.client.getWindow().getScaledWidth();
  // int scaledHeight = this.client.getWindow().getScaledHeight();
  // RenderSystem.color4f(1.0F, 1.0F, 1.0F, smooth);
  // this.client.getTextureManager().bindTexture(FREEZING_ICON);
  // DrawableHelper.drawTexture(matrixStack, (scaledWidth / 2) - 9, scaledHeight -
  // 55, 0.0F, 0.0F, 18, 18, 18, 18);
  // RenderSystem.disableBlend();
  // }

}