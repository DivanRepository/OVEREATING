package com.overatingplus.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.overatingplus.ModConfig;
import com.overatingplus.attachment.OversaturationAttachments;
import com.overatingplus.attachment.OversaturationData;
import com.overatingplus.logic.OversaturationLogic;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.Collection;

public final class ExtraWeightCommand {
    // Use a fixed max for command argument to avoid config dependency during registration
    private static final float COMMAND_MAX_POINTS = 20000f;

    private static final DynamicCommandExceptionType ERROR_NO_PLAYERS =
            new DynamicCommandExceptionType(count -> Component.translatable("command.extraweight.error.no_players", count));

    private ExtraWeightCommand() {
    }

    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        LiteralArgumentBuilder<CommandSourceStack> extraweight = Commands.literal("extraweight")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.literal("get")
                                .executes(ctx -> getPoints(ctx, EntityArgument.getPlayers(ctx, "targets"))))
                        .then(Commands.literal("set")
                                .then(Commands.argument("value", FloatArgumentType.floatArg(0f, COMMAND_MAX_POINTS))
                                        .executes(ctx -> setPoints(ctx, EntityArgument.getPlayers(ctx, "targets"), FloatArgumentType.getFloat(ctx, "value")))))
                        .then(Commands.literal("add")
                                .then(Commands.argument("value", FloatArgumentType.floatArg(0f, COMMAND_MAX_POINTS))
                                        .executes(ctx -> addPoints(ctx, EntityArgument.getPlayers(ctx, "targets"), FloatArgumentType.getFloat(ctx, "value")))))
                        .then(Commands.literal("sub")
                                .then(Commands.argument("value", FloatArgumentType.floatArg(0f, COMMAND_MAX_POINTS))
                                        .executes(ctx -> subPoints(ctx, EntityArgument.getPlayers(ctx, "targets"), FloatArgumentType.getFloat(ctx, "value"))))));

        dispatcher.register(extraweight);
    }

    private static int getPoints(CommandContext<CommandSourceStack> ctx, Collection<ServerPlayer> targets) throws CommandSyntaxException {
        if (targets.isEmpty()) {
            throw ERROR_NO_PLAYERS.create(0);
        }

        for (ServerPlayer player : targets) {
            OversaturationData data = OversaturationAttachments.get(player);
            int level = OversaturationLogic.getLevel(data.getPoints());
            ctx.getSource().sendSuccess(() -> Component.translatable(
                    "command.extraweight.get",
                    player.getDisplayName(),
                    String.format("%.1f", data.getPoints()),
                    level,
                    ModConfig.SLOWDOWN_START_LEVEL.get(),
                    ModConfig.MAX_FAT_ARMOR_HP.get()
            ), false);
        }

        return targets.size();
    }

    private static int setPoints(CommandContext<CommandSourceStack> ctx, Collection<ServerPlayer> targets, float value) throws CommandSyntaxException {
        if (targets.isEmpty()) {
            throw ERROR_NO_PLAYERS.create(0);
        }

        for (ServerPlayer player : targets) {
            OversaturationData data = OversaturationAttachments.get(player);
            int previousLevel = OversaturationLogic.getLevel(data.getPoints());
            data.setPoints(OversaturationLogic.clampPoints(value));
            int level = OversaturationLogic.getLevel(value);
            OversaturationLogic.updateFatArmor(player, data, level, previousLevel);
            OversaturationLogic.clampFatArmorToStack(data, level);
            OversaturationAttachments.set(player, data);
            OversaturationLogic.syncPlayerState(player);
            player.syncData(OversaturationAttachments.OVERSATURATION.get());

            final int finalLevel = level;
            ctx.getSource().sendSuccess(() -> Component.translatable(
                    "command.extraweight.set",
                    player.getDisplayName(),
                    String.format("%.1f", value),
                    finalLevel
            ), true);
        }

        return targets.size();
    }

    private static int addPoints(CommandContext<CommandSourceStack> ctx, Collection<ServerPlayer> targets, float value) throws CommandSyntaxException {
        if (targets.isEmpty()) {
            throw ERROR_NO_PLAYERS.create(0);
        }

        for (ServerPlayer player : targets) {
            OversaturationData data = OversaturationAttachments.get(player);
            int previousLevel = OversaturationLogic.getLevel(data.getPoints());
            data.setPoints(OversaturationLogic.applyPointGain(data.getPoints(), value));
            int level = OversaturationLogic.getLevel(data.getPoints());
            OversaturationLogic.updateFatArmor(player, data, level, previousLevel);
            OversaturationLogic.clampFatArmorToStack(data, level);
            OversaturationAttachments.set(player, data);
            OversaturationLogic.syncPlayerState(player);
            player.syncData(OversaturationAttachments.OVERSATURATION.get());

            final float finalPoints = data.getPoints();
            final int finalLevel = level;
            ctx.getSource().sendSuccess(() -> Component.translatable(
                    "command.extraweight.add",
                    player.getDisplayName(),
                    String.format("%.1f", value),
                    String.format("%.1f", finalPoints),
                    finalLevel
            ), true);
        }

        return targets.size();
    }

    private static int subPoints(CommandContext<CommandSourceStack> ctx, Collection<ServerPlayer> targets, float value) throws CommandSyntaxException {
        if (targets.isEmpty()) {
            throw ERROR_NO_PLAYERS.create(0);
        }

        for (ServerPlayer player : targets) {
            OversaturationData data = OversaturationAttachments.get(player);
            int previousLevel = OversaturationLogic.getLevel(data.getPoints());
            // Subtract points, clamping to 0
            float newPoints = Math.max(0f, data.getPoints() - value);
            data.setPoints(OversaturationLogic.clampPoints(newPoints));
            int level = OversaturationLogic.getLevel(data.getPoints());
            OversaturationLogic.updateFatArmor(player, data, level, previousLevel);
            OversaturationLogic.clampFatArmorToStack(data, level);
            OversaturationAttachments.set(player, data);
            OversaturationLogic.syncPlayerState(player);
            player.syncData(OversaturationAttachments.OVERSATURATION.get());

            final float finalPoints = data.getPoints();
            final int finalLevel = level;
            ctx.getSource().sendSuccess(() -> Component.translatable(
                    "command.extraweight.sub",
                    player.getDisplayName(),
                    String.format("%.1f", value),
                    String.format("%.1f", finalPoints),
                    finalLevel
            ), true);
        }

        return targets.size();
    }
}
