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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements ServerPlayerAccess {

    @Unique
    private ThirstManager thirstManager = ((ThirstManagerAccess) this).getThirstManager();
    @Unique
    private int syncedThirstLevel = -99999999;
    @Unique
    private int compatSyncTicks = 0;

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "playerTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;tick()V", shift = Shift.AFTER))
    private void playerTickMixin(CallbackInfo info) {
        if (this.syncedThirstLevel != this.thirstManager.getThirstLevel() && this.thirstManager.hasThirst()) {
            ThirstServerPacket.writeS2CThirstUpdatePacket((ServerPlayerEntity) (Object) this);
            this.syncedThirstLevel = thirstManager.getThirstLevel();
        }
        if (compatSyncTicks > 0) {
            compatSyncTicks--;
            if (compatSyncTicks == 1) {
                ThirstServerPacket.writeS2CExcludedSyncPacket((ServerPlayerEntity) (Object) this, thirstManager.hasThirst());
            }
        }
    }

    @Inject(method = "Lnet/minecraft/server/network/ServerPlayerEntity;teleport(Lnet/minecraft/server/world/ServerWorld;DDDFF)V", at = @At("TAIL"))
    private void teleportMixin(ServerWorld targetWorld, double x, double y, double z, float yaw, float pitch, CallbackInfo info) {
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
