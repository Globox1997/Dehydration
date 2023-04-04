// package net.dehydration.mixin.compat;

// import java.util.Collection;
// import java.util.Iterator;

// import com.mojang.brigadier.context.CommandContext;
// import com.mojang.brigadier.exceptions.CommandSyntaxException;

// import org.spongepowered.asm.mixin.Mixin;
// import org.spongepowered.asm.mixin.injection.Inject;
// import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
// import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
// import org.spongepowered.asm.mixin.injection.At;

// import io.github.apace100.apoli.power.PowerTypeReference;
// import io.github.apace100.origins.Origins;
// import io.github.apace100.origins.command.OriginCommand;
// import io.github.apace100.origins.origin.Origin;
// import io.github.apace100.origins.origin.OriginLayer;
// import net.dehydration.access.ThirstManagerAccess;
// import net.dehydration.network.ThirstServerPacket;
// import net.dehydration.thirst.ThirstManager;
// import net.minecraft.server.command.ServerCommandSource;
// import net.minecraft.server.network.ServerPlayerEntity;

// @SuppressWarnings("rawtypes")
// @Mixin(OriginCommand.class)
// public class OriginCommandMixin {

//     @Inject(method = "setOrigin", at = @At(value = "INVOKE", target = "Lio/github/apace100/origins/component/OriginComponent;sync()V"), locals = LocalCapture.CAPTURE_FAILSOFT, remap = false)
//     private static void setOriginMixin(CommandContext<ServerCommandSource> commandContext, CallbackInfoReturnable<Integer> info, Collection targets, OriginLayer originLayer, Origin origin,
//             ServerCommandSource serverCommandSource, int processedTargets, Iterator var6, ServerPlayerEntity target) throws CommandSyntaxException {
//         ThirstManager thirstManager = ((ThirstManagerAccess) target).getThirstManager();
//         boolean setThirst = true;
//         if (origin.hasPowerType(new PowerTypeReference<>(Origins.identifier("fire_immunity"))) && thirstManager.hasThirst())
//             setThirst = false;

//         thirstManager.setThirst(setThirst);
//         ThirstServerPacket.writeS2CExcludedSyncPacket(target, setThirst);

//     }
// }
