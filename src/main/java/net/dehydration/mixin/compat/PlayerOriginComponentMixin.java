// package net.dehydration.mixin.compat;

// import org.spongepowered.asm.mixin.Mixin;
// import org.spongepowered.asm.mixin.Shadow;
// import org.spongepowered.asm.mixin.injection.Inject;
// import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
// import org.spongepowered.asm.mixin.injection.At;

// import io.github.apace100.apoli.power.PowerTypeReference;
// import io.github.apace100.origins.Origins;
// import io.github.apace100.origins.component.PlayerOriginComponent;
// import io.github.apace100.origins.origin.Origin;
// import io.github.apace100.origins.origin.OriginLayer;
// import net.dehydration.access.ServerPlayerAccess;
// import net.dehydration.access.ThirstManagerAccess;
// import net.dehydration.network.ThirstServerPacket;
// import net.dehydration.thirst.ThirstManager;
// import net.minecraft.entity.player.PlayerEntity;
// import net.minecraft.server.network.ServerPlayerEntity;

// @Mixin(PlayerOriginComponent.class)
// public class PlayerOriginComponentMixin {

//     @Shadow
//     private PlayerEntity player;

//     @Inject(method = "setOrigin", at = @At("TAIL"), remap = false)
//     private void setOriginMixin(OriginLayer layer, Origin origin, CallbackInfo info) {
//         ThirstManager thirstManager = ((ThirstManagerAccess) player).getThirstManager();
//         boolean setThirst = true;
//         if (origin.hasPowerType(new PowerTypeReference<>(Origins.identifier("fire_immunity"))) && thirstManager.hasThirst())
//             setThirst = false;

//         thirstManager.setThirst(setThirst);
//         if (player instanceof ServerPlayerEntity) {
//             ThirstServerPacket.writeS2CExcludedSyncPacket((ServerPlayerEntity) player, setThirst);
//             ((ServerPlayerAccess) player).compatSync();
//         }
//     }

// }
