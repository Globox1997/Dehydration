package net.dehydration.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.dehydration.DehydrationMain;
import net.dehydration.access.PlayerAccess;
import net.dehydration.access.ThirstManagerAccess;
import net.dehydration.init.ConfigInit;
import net.dehydration.init.TagInit;
import net.dehydration.thirst.ThirstManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements ThirstManagerAccess, PlayerAccess {
    private ThirstManager thirstManager = new ThirstManager();

    @Override
    public ThirstManager getThirstManager() {
        return this.thirstManager;
    }

    @Shadow
    protected HungerManager hungerManager = new HungerManager();
    @Shadow
    private int sleepTimer;

    private int drinkTime = 0;

    public PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "Lnet/minecraft/entity/player/PlayerEntity;tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;update(Lnet/minecraft/entity/player/PlayerEntity;)V", shift = Shift.AFTER))
    private void tickMixin(CallbackInfo info) {
        if (this.thirstManager.hasThirst()) {
            this.thirstManager.update((PlayerEntity) (Object) this);
        }
    }

    @Inject(method = "Lnet/minecraft/entity/player/PlayerEntity;tickMovement()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;updateItems()V", shift = Shift.BEFORE))
    private void tickMovementMixin(CallbackInfo info) {
        if (this.getWorld().getDifficulty() == Difficulty.PEACEFUL && this.getWorld().getGameRules().getBoolean(GameRules.NATURAL_REGENERATION) && this.thirstManager.hasThirst()) {
            PlayerEntity player = (PlayerEntity) (Object) this;
            this.thirstManager.update(player);
            if (this.thirstManager.isNotFull() && this.age % 10 == 0) {
                this.thirstManager.setThirstLevel(this.thirstManager.getThirstLevel() + 1);
            }
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At(value = "TAIL"))
    private void readCustomDataFromTagMixin(NbtCompound tag, CallbackInfo info) {
        this.thirstManager.readNbt(tag);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At(value = "TAIL"))
    private void writeCustomDataToTagMixin(NbtCompound tag, CallbackInfo info) {
        this.thirstManager.writeNbt(tag);
    }

    @Inject(method = "Lnet/minecraft/entity/player/PlayerEntity;addExhaustion(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;addExhaustion(F)V", shift = Shift.AFTER))
    private void addExhaustionMixin(float exhaustion, CallbackInfo info) {
        if (this.thirstManager.hasThirst()) {
            if (ConfigInit.CONFIG.harder_nether && this.getWorld().getDimension().ultrawarm()) {
                exhaustion *= ConfigInit.CONFIG.nether_factor;
            }
            this.thirstManager.addDehydration(exhaustion / ConfigInit.CONFIG.hydrating_factor);
        }
    }

    @Inject(method = "Lnet/minecraft/entity/player/PlayerEntity;wakeUp(ZZ)V", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerEntity;sleepTimer:I"))
    private void wakeUpMixin(boolean bl, boolean updateSleepingPlayers, CallbackInfo info) {
        if (!this.getWorld().isClient() && this.thirstManager.hasThirst() && this.sleepTimer >= 100) {
            int thirstLevel = this.thirstManager.getThirstLevel();
            int hungerLevel = this.hungerManager.getFoodLevel();
            int thirstConsumption = ConfigInit.CONFIG.sleep_thirst_consumption;
            int hungerConsumption = ConfigInit.CONFIG.sleep_hunger_consumption;
            this.thirstManager.setThirstLevel(thirstLevel >= thirstConsumption ? thirstLevel - thirstConsumption : 0);
            this.hungerManager.setFoodLevel(hungerLevel >= hungerConsumption ? hungerLevel - hungerConsumption : 0);
        }
    }

    @Inject(method = "eatFood", at = @At(value = "HEAD"))
    private void eatFoodMixin(World world, ItemStack stack, CallbackInfoReturnable<ItemStack> info) {
        int thirstQuench = 0;
        if (stack.isIn(TagInit.HYDRATING_STEW)) {
            thirstQuench = ConfigInit.CONFIG.stew_thirst_quench;
        }
        if (stack.isIn(TagInit.HYDRATING_FOOD)) {
            thirstQuench = ConfigInit.CONFIG.food_thirst_quench;
        }
        if (stack.isIn(TagInit.HYDRATING_DRINKS)) {
            thirstQuench = ConfigInit.CONFIG.drinks_thirst_quench;
        }
        if (stack.isIn(TagInit.STRONGER_HYDRATING_STEW)) {
            thirstQuench = ConfigInit.CONFIG.stronger_stew_thirst_quench;
        }
        if (stack.isIn(TagInit.STRONGER_HYDRATING_FOOD)) {
            thirstQuench = ConfigInit.CONFIG.stronger_food_thirst_quench;
        }
        if (stack.isIn(TagInit.STRONGER_HYDRATING_DRINKS)) {
            thirstQuench = ConfigInit.CONFIG.stronger_drinks_thirst_quench;
        }

        for (int i = 0; i < DehydrationMain.HYDRATION_TEMPLATES.size(); i++) {
            if (DehydrationMain.HYDRATION_TEMPLATES.get(i).containsItem(stack.getItem())) {
                thirstQuench = DehydrationMain.HYDRATION_TEMPLATES.get(i).getHydration();
                break;
            }
        }
        if (thirstQuench > 0)
            ((ThirstManagerAccess) (PlayerEntity) (Object) this).getThirstManager().add(thirstQuench);
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
