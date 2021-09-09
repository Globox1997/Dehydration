package net.dehydration.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import io.github.apace100.apoli.power.PowerTypeReference;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.component.PlayerOriginComponent;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData.ValidationException;
import net.dehydration.config.DehydrationConfig;
import net.dehydration.init.ConfigInit;
import net.dehydration.network.ThirstServerPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(PlayerOriginComponent.class)
public class PlayerOriginComponentMixin {

    @Shadow
    private PlayerEntity player;

    @Inject(method = "setOrigin", at = @At("TAIL"), remap = false)
    private void setOriginMixin(OriginLayer layer, Origin origin, CallbackInfo info) throws ValidationException {
        boolean listContainsPlayer = ConfigInit.CONFIG.excluded_names.contains(player.getName().asString());
        if (origin.hasPowerType(new PowerTypeReference<>(Origins.identifier("fire_immunity")))) {
            if (!listContainsPlayer) {
                ConfigInit.CONFIG.excluded_names.add(player.getName().asString());
            }
        } else if (listContainsPlayer) {
            ConfigInit.CONFIG.excluded_names.remove(player.getName().asString());
        }
        AutoConfig.getConfigHolder(DehydrationConfig.class).save();
        ThirstServerPacket.writeS2CExcludedSyncPacket((ServerPlayerEntity) player);
    }

}
