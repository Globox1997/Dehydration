package net.dehydration.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.dehydration.access.ThristManagerAccess;
import net.dehydration.effect.DehydrationEffect;
import net.dehydration.init.ConfigInit;
import net.dehydration.init.EffectInit;
import net.dehydration.init.EnchantmentInit;
import net.dehydration.thirst.ThirstManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements ThristManagerAccess {
  private ThirstManager thirstManager = new ThirstManager();

  @Override
  public ThirstManager getThirstManager(PlayerEntity player) {
    return this.thirstManager;
  }

  private int dehydrationTimer;
  private int coolingDownTimer;

  public PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
    super(entityType, world);
  }

  @Inject(method = "Lnet/minecraft/entity/player/PlayerEntity;tick()V", at = @At("TAIL"))
  public void tickMixin(CallbackInfo info) {
    PlayerEntity playerEntity = (PlayerEntity) (Object) this;
    if (!playerEntity.isCreative()) {
      if (this.world.getBiome(this.getBlockPos()).getTemperature() >= 2.0F
          && DehydrationEffect.wearsArmorModifier(playerEntity) != ConfigInit.CONFIG.wears_armor_modifier * 4
          && !this.isTouchingWaterOrRain()) {
        if (this.world.isSkyVisible(this.getBlockPos()) && this.world.isDay() && EnchantmentHelper
            .getLevel(EnchantmentInit.HYDRATION_ENCHANTMENT, this.getEquippedStack(EquipmentSlot.CHEST)) == 0) {
          dehydrationTimer++;
          if (dehydrationTimer % 40 == 0) {
            thirstManager.addDehydration(0.5F);
          }
          if (dehydrationTimer >= ConfigInit.CONFIG.dehydration_tick_interval) {
            if (thirstManager.getThirstLevel() < 17) {
              this.addStatusEffect(new StatusEffectInstance(EffectInit.DEHYDRATION,
                  ConfigInit.CONFIG.dehydration_damage_effect_time, 0, true, false));
            }
            dehydrationTimer = 0;
          }
        }
      } else if (dehydrationTimer > 0) {
        dehydrationTimer = 0;
      }
      if (this.hasStatusEffect(EffectInit.DEHYDRATION)) {
        if (this.world.isNight() || this.world.getBiome(this.getBlockPos()).getTemperature() <= 0.0F
            || thirstManager.getThirstLevel() > 17 || this.isTouchingWaterOrRain()) {
          coolingDownTimer++;
          if (coolingDownTimer >= ConfigInit.CONFIG.cooling_down_interval) {
            int coldDuration = this.getStatusEffect(EffectInit.DEHYDRATION).getDuration();
            this.removeStatusEffect(EffectInit.DEHYDRATION);
            if (coldDuration > ConfigInit.CONFIG.cooling_down_tick_decrease) {
              this.addStatusEffect(new StatusEffectInstance(EffectInit.DEHYDRATION,
                  coldDuration - ConfigInit.CONFIG.cooling_down_tick_decrease, 0, true, false));
              thirstManager.addDehydration(1F);
            }
            thirstManager.addDehydration(1F);
            coolingDownTimer = 0;
          }
        }
      }
    }
  }

  @Inject(method = "Lnet/minecraft/entity/player/PlayerEntity;tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;update(Lnet/minecraft/entity/player/PlayerEntity;)V", shift = Shift.AFTER))
  private void tickMixinTwo(CallbackInfo info) {
    PlayerEntity player = (PlayerEntity) (Object) this;
    this.thirstManager.update(player);
  }

  @Inject(method = "Lnet/minecraft/entity/player/PlayerEntity;tickMovement()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;updateItems()V", shift = Shift.BEFORE))
  private void tickMovementMixin(CallbackInfo info) {
    if (this.world.getDifficulty() == Difficulty.PEACEFUL
        && this.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION)) {
      PlayerEntity player = (PlayerEntity) (Object) this;
      this.thirstManager.update(player);
      if (this.thirstManager.isNotFull() && this.age % 16 == 0) { // Test; Normal % 10
        this.thirstManager.setThirstLevel(this.thirstManager.getThirstLevel() + 1);
      }
    }
  }

  @Inject(method = "Lnet/minecraft/entity/player/PlayerEntity;readCustomDataFromTag(Lnet/minecraft/nbt/CompoundTag;)V", at = @At(value = "TAIL"))
  private void readCustomDataFromTagMixin(CompoundTag tag, CallbackInfo info) {
    this.thirstManager.fromTag(tag);
  }

  @Inject(method = "Lnet/minecraft/entity/player/PlayerEntity;writeCustomDataToTag(Lnet/minecraft/nbt/CompoundTag;)V", at = @At(value = "TAIL"))
  private void writeCustomDataToTagMixin(CompoundTag tag, CallbackInfo info) {
    this.thirstManager.toTag(tag);
  }

  @Inject(method = "Lnet/minecraft/entity/player/PlayerEntity;addExhaustion(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;addExhaustion(F)V", shift = Shift.AFTER))
  private void addExhaustionMixin(float exhaustion, CallbackInfo info) {
    this.thirstManager.addDehydration(exhaustion);
  }

}
