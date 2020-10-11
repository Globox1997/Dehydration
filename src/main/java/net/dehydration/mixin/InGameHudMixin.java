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
import net.minecraft.tag.FluidTags;
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

  private float smoothThirstRendering;
  private static final Identifier HEATING_ICON = new Identifier("dehydration:textures/misc/dehydration.png");
  private static final Identifier THIRST_ICON = new Identifier("dehydration:textures/misc/thirst.png");
  private static int wearsArmorModifier = ConfigInit.CONFIG.wears_armor_modifier;
  private static int dehydrationTickInterval = ConfigInit.CONFIG.dehydration_tick_interval;
  private static boolean enableBlackOutline = ConfigInit.CONFIG.enable_black_outline;
  private static int heatIconX = ConfigInit.CONFIG.heat_icon_x;
  private static int heatIconY = ConfigInit.CONFIG.heat_icon_y;

  public InGameHudMixin(MinecraftClient client) {
    this.client = client;
  }

  @Inject(method = "Lnet/minecraft/client/gui/hud/InGameHud;render(Lnet/minecraft/client/util/math/MatrixStack;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbar(FLnet/minecraft/client/util/math/MatrixStack;)V"))
  private void renderHeatingIcon(MatrixStack matrixStack, float f, CallbackInfo info) {
    PlayerEntity playerEntity = client.player;
    if (!playerEntity.isCreative()) {
      if (playerEntity.world.getBiome(playerEntity.getBlockPos()).getTemperature() >= 2.0F
          && DehydrationEffect.wearsArmorModifier(playerEntity) != wearsArmorModifier * 4
          && playerEntity.world.isSkyVisible(playerEntity.getBlockPos()) && playerEntity.world.isDay()) {
        if (smoothThirstRendering < 1.0F) {
          smoothThirstRendering = smoothThirstRendering
              + (1.0F / (float) (dehydrationTickInterval + wearsArmorModifier));
        }
        if (smoothThirstRendering > 1.0F) {
          smoothThirstRendering = 1.0F;
        }
        this.renderHeatingIconOverlay(matrixStack, smoothThirstRendering);
      } else if (smoothThirstRendering > 0.0F) {
        this.renderHeatingIconOverlay(matrixStack, smoothThirstRendering);
        smoothThirstRendering = smoothThirstRendering - 0.01F;
      }

    }
  }

  private void renderHeatingIconOverlay(MatrixStack matrixStack, float smooth) {
    this.client.getTextureManager().bindTexture(HEATING_ICON);
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, smooth);
    DrawableHelper.drawTexture(matrixStack, (scaledWidth / 2) - heatIconX, scaledHeight - heatIconY, 0.0F, 0.0F, 10, 10,
        10, 10);
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
          int airplayer = playerEntity.getAir();
          int airplayermax = playerEntity.getMaxAir();
          if (playerEntity.isSubmergedIn(FluidTags.WATER) || airplayer < airplayermax) {
            variable_three = variable_three - 10;
          }
          int uppderCoord = 0;
          if (enableBlackOutline) {
            uppderCoord = uppderCoord + 9;
          }
          variable_two = width - variable_one * 8 - 9;
          this.client.getTextureManager().bindTexture(THIRST_ICON);
          if (variable_one * 2 + 1 < thirst) {
            this.drawTexture(matrices, variable_two, variable_three, 0, uppderCoord, 9, 9); // Big icon
          }
          if (variable_one * 2 + 1 == thirst) {
            this.drawTexture(matrices, variable_two, variable_three, 9, uppderCoord, 9, 9); // Small icon
          }
        }
      }
    }
  }

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

}