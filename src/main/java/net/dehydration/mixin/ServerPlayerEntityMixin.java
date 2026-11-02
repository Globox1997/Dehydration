package net.dehydration.mixin;

import com.mojang.authlib.GameProfile;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.spongepowered.asm.mixin.injection.At;

import net.dehydration.access.ServerPlayerAccess;
import net.dehydration.access.ThirstManagerAccess;
import net.dehydration.network.ThirstServerPacket;
import net.dehydration.thirst.ThirstManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.level.Level;
import java.util.Set;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin extends Player implements ServerPlayerAccess {

    @Unique
    private ThirstManager thirstManager = ((ThirstManagerAccess) this).getThirstManager();
    @Unique
    private int syncedThirstLevel = -99999999;
    @Unique
    private int compatSyncTicks = 0;

    public ServerPlayerEntityMixin(Level world, GameProfile gameProfile) {
        super(world, gameProfile);
    }

    @Inject(method = "doTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;tick()V", shift = Shift.AFTER))
    private void playerTickMixin(CallbackInfo info) {
        if (this.syncedThirstLevel != this.thirstManager.getThirstLevel() && this.thirstManager.hasThirst()) {
            ThirstServerPacket.writeS2CThirstUpdatePacket((ServerPlayer) (Object) this);
            this.syncedThirstLevel = thirstManager.getThirstLevel();
        }
        if (compatSyncTicks > 0) {
            compatSyncTicks--;
            if (compatSyncTicks == 1) {
                ThirstServerPacket.writeS2CExcludedSyncPacket((ServerPlayer) (Object) this, thirstManager.hasThirst());
            }
        }
    }

    @Inject(method = "doTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;tick(Lnet/minecraft/server/level/ServerPlayer;)V", shift = Shift.AFTER))
    private void tickMixin(CallbackInfo info) {
        if (this.thirstManager.hasThirst()) {
            this.thirstManager.update(this);
            if (this.isSprinting() && !this.isCreative() && !this.isSpectator() && this.thirstManager.getThirstLevel() < 6) {
                this.setSprinting(false);
            }
        }
    }

    @Inject(method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDLjava/util/Set;FFZ)Z", at = @At("RETURN"))
    private void teleportMixin(ServerLevel targetWorld, double x, double y, double z, Set<Relative> relatives, float yaw, float pitch, boolean resetCamera, CallbackInfoReturnable<Boolean> info) {
        this.syncedThirstLevel = -1;
    }

    @Override
    public void compatSync() {
        this.compatSyncTicks = 5;
    }

    @Override
    public void setSyncedThirstLevel(int syncedThirstLevel) {
        this.syncedThirstLevel = syncedThirstLevel;
    }

}
