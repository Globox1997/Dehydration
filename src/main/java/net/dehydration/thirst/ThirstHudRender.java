package net.dehydration.thirst;

import org.jetbrains.annotations.Nullable;

import net.dehydration.access.ThirstManagerAccess;
import net.dehydration.init.ConfigInit;
import net.dehydration.init.EffectInit;
import net.dehydration.init.RenderInit;
import net.dehydration.item.LeatherFlask;
import net.dehydration.misc.ThirstTooltipData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class ThirstHudRender {

    private static float flashAlpha = 0f;
    private static float otherFlashAlpha = 0f;

    // Could implement HudRenderCallback
    public static void renderThirstHud(GuiGraphicsExtractor context, Minecraft client, Player playerEntity, int scaledWidth, int scaledHeight, int ticks) {
        if (playerEntity != null && !playerEntity.isInvulnerable() && !playerEntity.isCreative() && !playerEntity.isSpectator() && !client.gui.hud.isHidden()) {
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
                        if (!playerEntity.getMainHandItem().isEmpty() && playerEntity.getMainHandItem().getTooltipImage().isPresent()
                                && playerEntity.getMainHandItem().getTooltipImage().get() instanceof ThirstTooltipData) {
                            itemStack = playerEntity.getMainHandItem();
                        } else if (!playerEntity.getOffhandItem().isEmpty() && playerEntity.getOffhandItem().getTooltipImage().isPresent()
                                && playerEntity.getOffhandItem().getTooltipImage().get() instanceof ThirstTooltipData) {
                            itemStack = playerEntity.getOffhandItem();
                        }
                    }
                    if (itemStack != null) {
                        otherFlashAlpha = otherFlashAlpha += Mth.PI / (48F);
                        flashAlpha = (Mth.cos(otherFlashAlpha + Mth.PI) + 1f) / 2f;
                    } else if (otherFlashAlpha > 0.01F) {
                        otherFlashAlpha = 0.0F;
                        flashAlpha = 0.0F;
                    }

                    // Render ui droplets
                    for (variable_one = 0; variable_one < 10; ++variable_one) {
                        variable_three = height;
                        if (playerEntity.hasEffect(EffectInit.THIRST) && ticks % (thirst * 3 + 1) == 0) {
                            variable_three = height + (playerEntity.level().getRandom().nextInt(3) - 1); // bouncy
                        } else if (ticks % (thirst * 8 + 3) == 0) {
                            variable_three = height + (playerEntity.level().getRandom().nextInt(3) - 1); // bouncy
                        }
                        int uppderCoord = 9;
                        if (ConfigInit.CONFIG.other_droplet_texture) {
                            uppderCoord = uppderCoord + 9;
                        }

                        int beneathCoord = 0;
                        if (playerEntity.hasEffect(EffectInit.THIRST)) {
                            beneathCoord = 36;
                        }

                        variable_two = width - variable_one * 8 - 9;
                        variable_two = variable_two + ConfigInit.CONFIG.hud_x;
                        variable_three = variable_three + ConfigInit.CONFIG.hud_y;

                        context.blit(RenderPipelines.GUI_TEXTURED, RenderInit.THIRST_ICON, variable_two, variable_three, 0, 0, 9, 9, 256, 256); // Background
                        if (variable_one * 2 + 1 < thirst) {
                            context.blit(RenderPipelines.GUI_TEXTURED, RenderInit.THIRST_ICON, variable_two, variable_three, beneathCoord, uppderCoord, 9, 9, 256, 256); // Big icon
                        }
                        if (variable_one * 2 + 1 == thirst) {
                            context.blit(RenderPipelines.GUI_TEXTURED, RenderInit.THIRST_ICON, variable_two, variable_three, beneathCoord + 9, uppderCoord, 9, 9, 256, 256); // Small icon
                        }
                        // Show item thirst quench
                        if (variable_one >= thirst / 2) {
                            if (itemStack != null) {
                                int flashColor = ARGB.white(flashAlpha);
                                int thirstQuench = ((ThirstTooltipData) itemStack.getTooltipImage().get()).getThirstQuench();
                                if (itemStack.getItem() instanceof LeatherFlask)
                                    thirstQuench = ConfigInit.CONFIG.flask_thirst_quench;
                                int quality = ((ThirstTooltipData) itemStack.getTooltipImage().get()).getDrinkQuality();
                                if (variable_one < (thirst + thirstQuench) / 2) {
                                    context.blit(RenderPipelines.GUI_TEXTURED, RenderInit.THIRST_ICON, variable_two, variable_three, quality * 18, 9, 9, 9, 256, 256, flashColor);
                                } else if ((thirst + thirstQuench) % 2 != 0 && variable_one < (thirst + thirstQuench) / 2 + 1) {
                                    context.blit(RenderPipelines.GUI_TEXTURED, RenderInit.THIRST_ICON, variable_two, variable_three, quality * 18 + 9, 9, 9, 9, 256, 256, flashColor);
                                }
                            }
                        }
                        // Freezing
                        if (playerEntity.getTicksFrozen() > 0) {
                            int frozenColor = ARGB.white(playerEntity.getPercentFrozen());
                            if (variable_one * 2 + 1 < thirst) {
                                context.blit(RenderPipelines.GUI_TEXTURED, RenderInit.THIRST_ICON, variable_two, variable_three, 54, uppderCoord, 9, 9, 256, 256, frozenColor);
                            }
                            if (variable_one * 2 + 1 == thirst) {
                                context.blit(RenderPipelines.GUI_TEXTURED, RenderInit.THIRST_ICON, variable_two, variable_three, 54 + 9, uppderCoord, 9, 9, 256, 256, frozenColor);
                            }
                        }
                    }
                }
            }
        }
    }

    public static boolean shouldRenderThirstHud(@Nullable Player playerEntity) {
        return playerEntity != null && !playerEntity.isInvulnerable() && !playerEntity.isCreative() && !playerEntity.isSpectator()
                && ((ThirstManagerAccess) playerEntity).getThirstManager().hasThirst() && getHeartCount(playerEntity.getVehicle()) == 0;
    }

    private static int getHeartCount(@Nullable Entity entity) {
        if (entity == null || !entity.showVehicleHealth() || !(entity instanceof LivingEntity livingEntity)) {
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
