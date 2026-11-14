package net.dehydration.mixin;

import net.dehydration.access.PlayerAccess;
import net.dehydration.access.ThirstManagerAccess;
import net.dehydration.init.ConfigInit;
import net.dehydration.thirst.ThirstManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerEntityMixin extends LivingEntity implements ThirstManagerAccess, PlayerAccess {
    @Unique
    private final ThirstManager thirstManager = new ThirstManager();

    @Override
    public ThirstManager getThirstManager() {
        return this.thirstManager;
    }

    @Shadow
    protected FoodData foodData;
    @Shadow
    private int sleepCounter;
    @Unique
    private int drinkTime = 0;

    public PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Shadow
    public abstract boolean isCreative();

    @Inject(method = "aiStep()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;tick()V", shift = Shift.BEFORE))
    private void tickMovementMixin(CallbackInfo info) {
        if (this.level() instanceof ServerLevel serverWorld && serverWorld.getDifficulty() == Difficulty.PEACEFUL && serverWorld.getGameRules().get(GameRules.NATURAL_HEALTH_REGENERATION) && this.thirstManager.hasThirst()) {
            if (this.thirstManager.isNotFull() && this.tickCount % 10 == 0) {
                this.thirstManager.setThirstLevel(this.thirstManager.getThirstLevel() + 1);
            }
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At(value = "TAIL"))
    private void readCustomDataFromTagMixin(ValueInput tag, CallbackInfo info) {
        this.thirstManager.readNbt(tag);
    }

    @Inject(method = "addAdditionalSaveData", at = @At(value = "TAIL"))
    private void writeCustomDataToTagMixin(ValueOutput tag, CallbackInfo info) {
        this.thirstManager.writeNbt(tag);
    }

    @Inject(method = "causeFoodExhaustion(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V", shift = Shift.AFTER))
    private void addExhaustionMixin(float exhaustion, CallbackInfo info) {
        if (this.thirstManager.hasThirst()) {
            if (ConfigInit.CONFIG.harder_nether && this.level().environmentAttributes().getValue(EnvironmentAttributes.WATER_EVAPORATES, this.blockPosition())) {
                exhaustion *= ConfigInit.CONFIG.nether_factor;
            }
            this.thirstManager.addDehydration(exhaustion / ConfigInit.CONFIG.hydrating_factor);
        }
    }

    @Inject(method = "stopSleepInBed(ZZ)V", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Player;sleepCounter:I"))
    private void wakeUpMixin(boolean bl, boolean updateSleepingPlayers, CallbackInfo info) {
        if (!this.level().isClientSide() && this.thirstManager.hasThirst() && this.sleepCounter >= 100) {
            int thirstLevel = this.thirstManager.getThirstLevel();
            int hungerLevel = this.foodData.getFoodLevel();
            int thirstConsumption = ConfigInit.CONFIG.sleep_thirst_consumption;
            int hungerConsumption = ConfigInit.CONFIG.sleep_hunger_consumption;
            this.thirstManager.setThirstLevel(thirstLevel >= thirstConsumption ? thirstLevel - thirstConsumption : 0);
            this.foodData.setFoodLevel(hungerLevel >= hungerConsumption ? hungerLevel - hungerConsumption : 0);
        }
    }

    @Override
    public void setDrinkTime(int time) {
        this.drinkTime = time;
    }

    @Override
    public int getDrinkTime() {
        return this.drinkTime;
    }

}
