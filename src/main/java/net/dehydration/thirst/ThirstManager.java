package net.dehydration.thirst;

import net.dehydration.init.ConfigInit;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.Difficulty;

public class ThirstManager {

    // Damage Type
    public static final RegistryKey<DamageType> THIRST = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("dehydration", "thirst"));

    public float dehydration;
    private boolean hasThirst = true;
    private int dehydrationTimer;
    private int thirstLevel = 20;

    public void add(int thirst) {
        this.thirstLevel = Math.min(thirst + this.thirstLevel, 20);
    }

    public void update(PlayerEntity player) {
        Difficulty difficulty = player.getWorld().getDifficulty();
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
                    player.damage(createDamageSource(player), ConfigInit.CONFIG.thirst_damage);
                }
                this.dehydrationTimer = 0;
            }
        } else {
            this.dehydrationTimer = 0;
        }
        if (!player.isCreative() && ConfigInit.CONFIG.special_effects) {
            if (thirstLevel == 2 && !player.hasStatusEffect(StatusEffects.HASTE)) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 409, 0, false, false, false));
            }
            if (thirstLevel == 0 && player.getHungerManager().getFoodLevel() == 0 && !player.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 409, 2, false, false, false));
            }
        }

    }

    public void readNbt(NbtCompound tag) {
        if (tag.contains("ThirstLevel", 99)) {
            this.thirstLevel = tag.getInt("ThirstLevel");
            this.dehydrationTimer = tag.getInt("ThirstTickTimer");
            this.dehydration = tag.getFloat("ThirstExhaustionLevel");
            this.hasThirst = tag.getBoolean("HasThirst");
        }
    }

    public void writeNbt(NbtCompound tag) {
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
        return entity.getDamageSources().create(THIRST, null);
    }

}
