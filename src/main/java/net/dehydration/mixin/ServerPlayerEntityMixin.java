package net.dehydration.mixin;

import com.mojang.authlib.GameProfile;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.spongepowered.asm.mixin.injection.At;

import net.dehydration.access.ServerPlayerAccess;
import net.dehydration.access.ThirstManagerAccess;
import net.dehydration.network.ThirstServerPacket;
import net.dehydration.thirst.ThirstManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin extends Player implements ServerPlayerAccess {

    @Unique
    private ThirstManager thirstManager = ((ThirstManagerAccess) this).getThirstManager();
    @Unique
    private int syncedThirstLevel = -99999999;
    @Unique
    private int compatSyncTicks = 0;

    public ServerPlayerEntityMixin(Level world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
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

    @Inject(method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDFF)V", at = @At("TAIL"))
    private void teleportMixin(ServerLevel targetWorld, double x, double y, double z, float yaw, float pitch, CallbackInfo info) {
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
