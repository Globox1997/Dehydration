package net.dehydration.thirst;

import org.jetbrains.annotations.Nullable;
import com.mojang.blaze3d.systems.RenderSystem;

import net.dehydration.access.ThirstManagerAccess;
import net.dehydration.init.ConfigInit;
import net.dehydration.init.EffectInit;
import net.dehydration.init.RenderInit;
import net.dehydration.item.LeatherFlask;
import net.dehydration.misc.ThirstTooltipData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ThirstHudRender {

    private static float flashAlpha = 0f;
    private static float otherFlashAlpha = 0f;

    // Could implement HudRenderCallback
    public static void renderThirstHud(DrawContext context, MinecraftClient client, PlayerEntity playerEntity, int scaledWidth, int scaledHeight, int ticks) {
        if (playerEntity != null && !playerEntity.isInvulnerable() && !playerEntity.isCreative() && !playerEntity.isSpectator() && !client.options.hudHidden) {
            ThirstManager thirstManager = ((ThirstManagerAccess) playerEntity).getThirstManager();
            if (thirstManager.hasThirst()) {
                int thirst = thirstManager.getThirstLevel();
                int variable_one;
                int variable_two;
                int variable_three;
                int height = scaledHeight - 49;
                int width = scaledWidth / 2 + 91;
                if (getHeartCount(playerEntity.getVehicle()) == 0) {

                    ItemStack itemStack = null;
                    if (ConfigInit.CONFIG.thirst_preview && thirst < 20) {
                        if (!playerEntity.getMainHandStack().isEmpty() && playerEntity.getMainHandStack().getTooltipData().isPresent()
                                && playerEntity.getMainHandStack().getTooltipData().get() instanceof ThirstTooltipData) {
                            itemStack = playerEntity.getMainHandStack();
                        } else if (!playerEntity.getOffHandStack().isEmpty() && playerEntity.getOffHandStack().getTooltipData().isPresent()
                                && playerEntity.getOffHandStack().getTooltipData().get() instanceof ThirstTooltipData) {
                            itemStack = playerEntity.getOffHandStack();
                        }
                    }
                    if (itemStack != null) {
                        otherFlashAlpha = otherFlashAlpha += MathHelper.PI / (48F);
                        flashAlpha = (MathHelper.cos(otherFlashAlpha + MathHelper.PI) + 1f) / 2f;
                    } else if (otherFlashAlpha > 0.01F) {
                        otherFlashAlpha = 0.0F;
                        flashAlpha = 0.0F;
                    }

                    // Render ui droplets
                    for (variable_one = 0; variable_one < 10; ++variable_one) {
                        variable_three = height;
                        if (playerEntity.hasStatusEffect(EffectInit.THIRST) && ticks % (thirst * 3 + 1) == 0) {
                            variable_three = height + (playerEntity.getWorld().getRandom().nextInt(3) - 1); // bouncy
                        } else if (ticks % (thirst * 8 + 3) == 0) {
                            variable_three = height + (playerEntity.getWorld().getRandom().nextInt(3) - 1); // bouncy
                        }
                        int uppderCoord = 9;
                        if (ConfigInit.CONFIG.other_droplet_texture) {
                            uppderCoord = uppderCoord + 9;
                        }

                        int beneathCoord = 0;
                        if (playerEntity.hasStatusEffect(EffectInit.THIRST)) {
                            beneathCoord = 36;
                        }

                        variable_two = width - variable_one * 8 - 9;
                        variable_two = variable_two + ConfigInit.CONFIG.hud_x;
                        variable_three = variable_three + ConfigInit.CONFIG.hud_y;

                        context.drawTexture(RenderInit.THIRST_ICON, variable_two, variable_three, 0, 0, 9, 9, 256, 256); // Background
                        if (variable_one * 2 + 1 < thirst) {
                            context.drawTexture(RenderInit.THIRST_ICON, variable_two, variable_three, beneathCoord, uppderCoord, 9, 9, 256, 256); // Big icon
                        }
                        if (variable_one * 2 + 1 == thirst) {
                            context.drawTexture(RenderInit.THIRST_ICON, variable_two, variable_three, beneathCoord + 9, uppderCoord, 9, 9, 256, 256); // Small icon
                        }
                        // Show item thirst quench
                        if (variable_one >= thirst / 2) {
                            if (itemStack != null) {
                                RenderSystem.enableBlend();
                                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, flashAlpha);
                                int thirstQuench = ((ThirstTooltipData) itemStack.getTooltipData().get()).getThirstQuench();
                                if (itemStack.getItem() instanceof LeatherFlask)
                                    thirstQuench = ConfigInit.CONFIG.flask_thirst_quench;
                                int quality = ((ThirstTooltipData) itemStack.getTooltipData().get()).getDrinkQuality();
                                if (variable_one < (thirst + thirstQuench) / 2) {
                                    context.drawTexture(RenderInit.THIRST_ICON, variable_two, variable_three, quality * 18, 9, 9, 9, 256, 256);
                                } else if ((thirst + thirstQuench) % 2 != 0 && variable_one < (thirst + thirstQuench) / 2 + 1) {
                                    context.drawTexture(RenderInit.THIRST_ICON, variable_two, variable_three, quality * 18 + 9, 9, 9, 9, 256, 256);
                                }
                                RenderSystem.disableBlend();
                                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                            }
                        }
                        // Freezing
                        if (playerEntity.getFrozenTicks() > 0) {
                            RenderSystem.enableBlend();
                            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, playerEntity.getFreezingScale());
                            if (variable_one * 2 + 1 < thirst) {
                                context.drawTexture(RenderInit.THIRST_ICON, variable_two, variable_three, 54, uppderCoord, 9, 9, 256, 256);
                            }
                            if (variable_one * 2 + 1 == thirst) {
                                context.drawTexture(RenderInit.THIRST_ICON, variable_two, variable_three, 54 + 9, uppderCoord, 9, 9, 256, 256);
                            }
                            RenderSystem.disableBlend();
                            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                        }
                    }
                }
            }
        }
    }

    public static boolean shouldRenderThirstHud(@Nullable PlayerEntity playerEntity) {
        return playerEntity != null && !playerEntity.isInvulnerable() && !playerEntity.isCreative() && !playerEntity.isSpectator()
                && ((ThirstManagerAccess) playerEntity).getThirstManager().hasThirst() && getHeartCount(playerEntity.getVehicle()) == 0;
    }

    private static int getHeartCount(@Nullable Entity entity) {
        if (entity == null || !entity.isLiving() || !(entity instanceof LivingEntity livingEntity)) {
            return 0;
        }
        float f = livingEntity.getMaxHealth();
        int i = (int) (f + 0.5f) / 2;
        if (i > 30) {
            i = 30;
        }
        return i;
    }

}
