package net.dehydration.thirst;

import net.dehydration.init.ConfigInit;
import net.dehydration.mixin.DamageSourceAccessor;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.Difficulty;

//Saturation for thirst?

public class ThirstManager implements DamageSourceAccessor {
  private int thirstLevel = 20;
  public float dehydration;
  private int dehydrationTimer;

  public void add(int thirst) {
    this.thirstLevel = Math.min(thirst + this.thirstLevel, 20);
  }

  public void update(PlayerEntity player) {
    Difficulty difficulty = player.world.getDifficulty();
    if (this.dehydration > 4.0F) {
      this.dehydration -= 4.0F;
      if (difficulty != Difficulty.PEACEFUL) {
        this.thirstLevel = Math.max(this.thirstLevel - 1, 0);
      }
    }
    if (this.thirstLevel <= 0) {
      ++this.dehydrationTimer;
      if (this.dehydrationTimer >= 90) {
        if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD
            || (player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL)) {
          player.damage(createDamageSource(), ConfigInit.CONFIG.thirst_damage);
        }
        this.dehydrationTimer = 0;
      }
    } else {
      this.dehydrationTimer = 0;
    }
    if (!player.isCreative()) {
      if (thirstLevel == 2 && !player.hasStatusEffect(StatusEffects.HASTE)) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 409, 0, false, false, false));
      }
      if (thirstLevel == 0 && player.getHungerManager().getFoodLevel() == 0
          && !player.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 409, 2, false, false, false));
      }
    }

  }

  public void readNbt(NbtCompound tag) {
    if (tag.contains("thirstLevel", 99)) {
      this.thirstLevel = tag.getInt("thirstLevel");
      this.dehydrationTimer = tag.getInt("thirstTickTimer");
      this.dehydration = tag.getFloat("thirstExhaustionLevel");
    }
  }

  public void writeNbt(NbtCompound tag) {
    tag.putInt("thirstLevel", this.thirstLevel);
    tag.putInt("thirstTickTimer", this.dehydrationTimer);
    tag.putFloat("thirstExhaustionLevel", this.dehydration);
  }

  public int getThirstLevel() {
    return this.thirstLevel;
  }

  public boolean isNotFull() {
    return this.thirstLevel < 20;
  }

  public void addDehydration(float dehydration) {
    this.dehydration = Math.min(this.dehydration + dehydration, 40.0F);
  }

  public void setThirstLevel(int thirstLevel) {
    this.thirstLevel = thirstLevel;
  }

  public DamageSource createDamageSource() {
    return ((DamageSourceAccessor) ((DamageSourceAccessor) new DamageSource("thirst")).setBypassesArmorAccess())
        .setUnblockableAccess();
  }

  @Override
  public DamageSource setBypassesArmorAccess() {
    throw new AssertionError("Access Error");
  }

  @Override
  public DamageSource setUnblockableAccess() {
    throw new AssertionError("Access Error");
  }

  // private int thirstLevel = 20;
  // private float thirstDehydrationLevel = 5.0F;
  // private float dehydration;
  // private int thirstDehydrationTimer;

  // public void add(int food, float f) {
  // this.thirstLevel = Math.min(food + this.thirstLevel, 20);
  // this.thirstDehydrationLevel = Math.min(this.thirstDehydrationLevel + (float)
  // food * f * 2.0F,
  // (float) this.thirstLevel);
  // }

  // public void update(PlayerEntity player) {
  // Difficulty difficulty = player.world.getDifficulty();
  // if (this.dehydration > 4.0F) {
  // this.dehydration -= 4.0F;
  // if (this.thirstDehydrationLevel > 0.0F) {
  // this.thirstDehydrationLevel = Math.max(this.thirstDehydrationLevel - 1.0F,
  // 0.0F);
  // } else if (difficulty != Difficulty.PEACEFUL) {
  // this.thirstLevel = Math.max(this.thirstLevel - 1, 0);
  // }
  // }

  // boolean bl =
  // player.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION);
  // if (bl && this.thirstDehydrationLevel > 0.0F && player.canFoodHeal() &&
  // this.thirstLevel >= 20) {
  // ++this.thirstDehydrationTimer;
  // if (this.thirstDehydrationTimer >= 10) {
  // float f = Math.min(this.thirstDehydrationLevel, 6.0F);
  // player.heal(f / 6.0F);
  // this.addDehydration(f);
  // this.thirstDehydrationTimer = 0;
  // }
  // } else if (bl && this.thirstLevel >= 18 && player.canFoodHeal()) {
  // ++this.thirstDehydrationTimer;
  // if (this.thirstDehydrationTimer >= 80) {
  // player.heal(1.0F);
  // this.addDehydration(6.0F);
  // this.thirstDehydrationTimer = 0;
  // }
  // } else if (this.thirstLevel <= 0) {
  // ++this.thirstDehydrationTimer;
  // if (this.thirstDehydrationTimer >= 80) {
  // if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD
  // || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
  // player.damage(createDamageSource(), ConfigInit.CONFIG.thirst_damage);
  // }

  // this.thirstDehydrationTimer = 0;
  // }
  // } else {
  // this.thirstDehydrationTimer = 0;
  // }
  // if (!player.isCreative()) {
  // if (thirstLevel == 2 && !player.hasStatusEffect(StatusEffects.HASTE)) {
  // player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 409, 0,
  // false, false, false));
  // }
  // if (thirstLevel == 0 && player.getHungerManager().getFoodLevel() == 0
  // && !player.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
  // player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE,
  // 409, 2, false, false, false));
  // }
  // }

  // }

  // public void fromTag(CompoundTag tag) {
  // if (tag.contains("thirstLevel", 99)) {
  // this.thirstLevel = tag.getInt("thirstLevel");
  // this.thirstDehydrationTimer = tag.getInt("thirstTickTimer");
  // this.thirstDehydrationLevel = tag.getFloat("thirstDehydrationLevel");
  // this.dehydration = tag.getFloat("dehydrationLevel");
  // }

  // }

  // public void toTag(CompoundTag tag) {
  // tag.putInt("thirstLevel", this.thirstLevel);
  // tag.putInt("thirstTickTimer", this.thirstDehydrationTimer);
  // tag.putFloat("thirstDehydrationLevel", this.thirstDehydrationLevel);
  // tag.putFloat("dehydrationLevel", this.dehydration);
  // }

  // public int getThirstLevel() {
  // return this.thirstLevel;
  // }

  // public boolean isNotFull() {
  // return this.thirstLevel < 20;
  // }

  // public void addDehydration(float dehydration) {
  // this.dehydration = Math.min(this.dehydration + dehydration, 40.0F);
  // }

  // public float getSaturationLevel() {
  // return this.thirstDehydrationLevel;
  // }

  // public void setThirstLevel(int thirstLevel) {
  // this.thirstLevel = thirstLevel;
  // }

  // @Environment(EnvType.CLIENT)
  // public void setSaturationLevelClient(float saturationLevel) {
  // this.thirstDehydrationLevel = saturationLevel;
  // }

  // public DamageSource createDamageSource() {
  // return ((DamageSourceAccessor) ((DamageSourceAccessor) new
  // DamageSource("thirst")).setBypassesArmorAccess())
  // .setUnblockableAccess();
  // }

  // @Override
  // public DamageSource setBypassesArmorAccess() {
  // throw new AssertionError("Access Error");
  // }

  // @Override
  // public DamageSource setUnblockableAccess() {
  // throw new AssertionError("Access Error");
  // }
}
