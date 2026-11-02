package net.dehydration.thirst;

import net.dehydration.init.ConfigInit;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class ThirstManager {

    // Damage Type
    public static final ResourceKey<DamageType> THIRST = ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath("dehydration", "thirst"));

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
            if (thirstLevel == 2 && !player.hasEffect(MobEffects.HASTE)) {
                player.addEffect(new MobEffectInstance(MobEffects.HASTE, 409, 0, false, false, false));
            }
            if (thirstLevel == 0 && player.getFoodData().getFoodLevel() == 0 && !player.hasEffect(MobEffects.MINING_FATIGUE)) {
                player.addEffect(new MobEffectInstance(MobEffects.MINING_FATIGUE, 409, 2, false, false, false));
            }
        }

    }

    public void readNbt(ValueInput tag) {
        if (tag.getInt("ThirstLevel").isPresent()) {
            this.thirstLevel = tag.getIntOr("ThirstLevel", 0);
            this.dehydrationTimer = tag.getIntOr("ThirstTickTimer", 0);
            this.dehydration = tag.getFloatOr("ThirstExhaustionLevel", 0.0F);
            this.hasThirst = tag.getBooleanOr("HasThirst", false);
        }
    }

    public void writeNbt(ValueOutput tag) {
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
