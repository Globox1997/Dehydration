package net.dehydration.mixin;

import com.mojang.authlib.GameProfile;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.netty.buffer.Unpooled;

import org.spongepowered.asm.mixin.injection.At;

import net.dehydration.access.ThristManagerAccess;
import net.dehydration.init.ConfigInit;
import net.dehydration.network.ThirstUpdateS2CPacket;
import net.dehydration.thirst.ThirstManager;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
  ThirstManager thirstManager = ((ThristManagerAccess) this).getThirstManager(this);
  private int syncedThirstLevel = -99999999;

  public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
    super(world, pos, yaw, profile);
  }

  @Inject(method = "playerTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;tick()V", shift = Shift.AFTER))
  public void playerTickMixin(CallbackInfo info) {
    if (this.syncedThirstLevel != this.thirstManager.getThirstLevel()
        && !ConfigInit.CONFIG.excluded_names.contains(this.getName().asString())) {
      PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
      data.writeIntArray(new int[] { this.getEntityId(), thirstManager.getThirstLevel() });
      ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, ThirstUpdateS2CPacket.THIRST_UPDATE, data);
      this.syncedThirstLevel = thirstManager.getThirstLevel();
    }
  }

  @Inject(method = "Lnet/minecraft/server/network/ServerPlayerEntity;copyFrom(Lnet/minecraft/server/network/ServerPlayerEntity;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;clone(Lnet/minecraft/entity/player/PlayerInventory;)V", shift = Shift.AFTER))
  public void copyFromMixinOne(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo info) {
    this.thirstManager = ((ThristManagerAccess) oldPlayer).getThirstManager(oldPlayer);
  }

  @Inject(method = "Lnet/minecraft/server/network/ServerPlayerEntity;copyFrom(Lnet/minecraft/server/network/ServerPlayerEntity;Z)V", at = @At(value = "TAIL"))
  public void copyFromMixinTwo(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo info) {
    this.syncedThirstLevel = -1;
  }

}
