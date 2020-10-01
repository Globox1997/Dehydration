package net.dehydration.thirst;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Difficulty;

public class ThirstManager {

  private int thirstLevel = 20;
  private float dehydration;
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
      if (this.dehydrationTimer >= 80) {
        if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD
            || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
          player.damage(createDamageSource(), 1.0F);
        }
        this.dehydrationTimer = 0;
      }
    } else {
      this.dehydrationTimer = 0;
    }
    if (thirstLevel == 2) {
      player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 400, 0, true, false));
    }

  }

  public void fromTag(CompoundTag tag) {
    if (tag.contains("thirstLevel", 99)) {
      this.thirstLevel = tag.getInt("thirstLevel");
      this.dehydrationTimer = tag.getInt("thirstTickTimer");
      this.dehydration = tag.getFloat("thirstExhaustionLevel");
    }
  }

  public void toTag(CompoundTag tag) {
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

  public static DamageSource createDamageSource() {
    return new EntityDamageSource("thirst", null);
  }
}
