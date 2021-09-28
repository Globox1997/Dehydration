package net.dehydration.init;

import net.dehydration.access.ThirstManagerAccess;
import net.dehydration.network.ThirstServerPacket;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

import java.util.Collection;
import java.util.Iterator;

public class CommandInit {

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register((CommandManager.literal("thirst").requires((serverCommandSource) -> {
                return serverCommandSource.hasPermissionLevel(3);
            })).then((CommandManager.argument("targets", EntityArgumentType.players()).then(CommandManager.literal("true").executes((commandContext) -> {
                return executeThirstCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), true);
            })).then(CommandManager.literal("false").executes((commandContext) -> {
                return executeThirstCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), false);
            })))));
        });
    }

    private static int executeThirstCommand(ServerCommandSource source, Collection<ServerPlayerEntity> targets, boolean setThirst) {
        Iterator<ServerPlayerEntity> var3 = targets.iterator();

        while (var3.hasNext()) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) var3.next();
            ((ThirstManagerAccess) serverPlayerEntity).getThirstManager(serverPlayerEntity).setThirst(setThirst);
            ThirstServerPacket.writeS2CExcludedSyncPacket(serverPlayerEntity, setThirst);
        }
        source.sendFeedback(new TranslatableText("commands.dehydration.changed"), true);

        return targets.size();
    }

}