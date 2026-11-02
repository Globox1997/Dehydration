package net.dehydration.thirst;

import net.dehydration.init.ConfigInit;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class ThirstManager {

    // Damage Type
    public static final ResourceKey<DamageType> THIRST = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("dehydration", "thirst"));

    public float dehydration;
    private boolean hasThirst = true;
    private int dehydrationTimer;
    private int thirstLevel = 20;

    public void add(int thirst) {
        this.thirstLevel = Math.min(thirst + this.thirstLevel, 20);
    }

    public void update(Player player) {
        Difficulty difficulty = player.level().getDifficulty();
        if (this.dehydration > 4.0F) {
            this.dehydration -= 4.0F;
            if (difficulty != Difficulty.PEACEFUL) {
                this.thirstLevel = Math.max(this.thirstLevel - 1, 0);
            }
        }
        if (this.thirstLevel <= 0) {
            ++this.dehydrationTimer;
            if (this.dehydrationTimer >= 90) {
                if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || (player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL)) {
                    player.hurt(createDamageSource(player), ConfigInit.CONFIG.thirst_damage);
                }
                this.dehydrationTimer = 0;
            }
        } else {
            this.dehydrationTimer = 0;
        }
        if (!player.isCreative() && ConfigInit.CONFIG.special_effects) {
            if (thirstLevel == 2 && !player.hasEffect(MobEffects.DIG_SPEED)) {
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 409, 0, false, false, false));
            }
            if (thirstLevel == 0 && player.getFoodData().getFoodLevel() == 0 && !player.hasEffect(MobEffects.DIG_SLOWDOWN)) {
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 409, 2, false, false, false));
            }
        }

    }

    public void readNbt(CompoundTag tag) {
        if (tag.contains("ThirstLevel", 99)) {
            this.thirstLevel = tag.getInt("ThirstLevel");
            this.dehydrationTimer = tag.getInt("ThirstTickTimer");
            this.dehydration = tag.getFloat("ThirstExhaustionLevel");
            this.hasThirst = tag.getBoolean("HasThirst");
        }
    }

    public void writeNbt(CompoundTag tag) {
        tag.putInt("ThirstLevel", this.thirstLevel);
        tag.putInt("ThirstTickTimer", this.dehydrationTimer);
        tag.putFloat("ThirstExhaustionLevel", this.dehydration);
        tag.putBoolean("HasThirst", this.hasThirst);
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

    public boolean hasThirst() {
        return this.hasThirst;
    }

    public void setThirst(boolean canHaveThirst) {
        this.hasThirst = canHaveThirst;
    }

    private DamageSource createDamageSource(Entity entity) {
        return entity.damageSources().source(THIRST, null);
    }

}
