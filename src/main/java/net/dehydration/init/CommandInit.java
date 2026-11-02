package net.dehydration.init;

import net.dehydration.access.ThirstManagerAccess;
import net.dehydration.network.ThirstServerPacket;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import java.util.Collection;

public class CommandInit {

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
            dispatcher.register((Commands.literal("thirst").requires(Commands.hasPermission(Commands.LEVEL_ADMINS))).then((Commands.argument("targets", EntityArgument.players()).then(Commands.literal("true").executes((commandContext) -> {
                return executeThirstCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), true);
            })).then(Commands.literal("false").executes((commandContext) -> {
                return executeThirstCommand(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), false);
            })))));
        });
    }

    private static int executeThirstCommand(CommandSourceStack source, Collection<ServerPlayer> targets, boolean setThirst) {

        for (ServerPlayer serverPlayerEntity : targets) {
            ((ThirstManagerAccess) serverPlayerEntity).getThirstManager().setThirst(setThirst);
            ThirstServerPacket.writeS2CExcludedSyncPacket(serverPlayerEntity, setThirst);
        }
        source.sendSuccess(() -> Component.translatable("commands.dehydration.changed"), true);

        return targets.size();
    }

}