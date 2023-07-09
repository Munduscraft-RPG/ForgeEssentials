package com.forgeessentials.permissions.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.FECommandParsingException;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

public class CommandPermissions extends ForgeEssentialsCommandBuilder
{
    public CommandPermissions(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public final String getPrimaryAlias()
    {
        return "perm";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
    {
        return new String[] { "fep", "p" };
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getPermissionNode()
    {
        return PermissionCommandParser.PERM;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder.then(Commands.literal("help").executes(CommandContext -> execute(CommandContext, "help")))
                .then(Commands.literal("user").executes(CommandContext -> execute(CommandContext, "user"))
                        .then(Commands.literal("help").executes(CommandContext -> execute(CommandContext, "user")))
                        .then(Commands.literal("perms")
                                .executes(CommandContext -> execute(CommandContext,
                                        "user&&" + StringArgumentType.getString(CommandContext, "player") + "&&perms")))
                        .then(Commands.argument("player", StringArgumentType.word())
                                .executes(CommandContext -> execute(CommandContext,
                                        "user&&" + StringArgumentType.getString(CommandContext, "player")))
                                .then(Commands.literal("zone").then(Commands
                                        .argument("zone", StringArgumentType.string()).suggests(SUGGEST_zones)
                                        .executes(CommandContext -> execute(CommandContext,
                                                "user&&" + StringArgumentType.getString(CommandContext, "player")
                                                        + "&&zone&&"
                                                        + StringArgumentType.getString(CommandContext, "zone")))
                                        .then(Commands.literal("group")
                                                .executes(CommandContext -> execute(CommandContext,
                                                        "user&&" + StringArgumentType.getString(CommandContext,
                                                                "player") + "&&zone&&"
                                                                + StringArgumentType.getString(CommandContext, "zone")
                                                                + "&&group"))
                                                .then(Commands.argument("arg", StringArgumentType.string())
                                                        .suggests(SUGGEST_parseUserGroupArgs)
                                                        .executes(CommandContext -> execute(CommandContext, "user&&"
                                                                + StringArgumentType.getString(CommandContext, "player")
                                                                + "&&zone&&"
                                                                + StringArgumentType.getString(CommandContext, "zone")
                                                                + "&&group&&"
                                                                + StringArgumentType.getString(CommandContext, "arg")))
                                                        .then(Commands.argument("group", StringArgumentType.string())
                                                                .suggests(SUGGEST_group)
                                                                .executes(CommandContext -> execute(CommandContext,
                                                                        "user&&" + StringArgumentType
                                                                                .getString(CommandContext, "player")
                                                                                + "&&zone&&"
                                                                                + StringArgumentType.getString(
                                                                                        CommandContext, "zone")
                                                                                + "&&group&&"
                                                                                + StringArgumentType.getString(
                                                                                        CommandContext, "arg")
                                                                                + "&&"
                                                                                + StringArgumentType.getString(
                                                                                        CommandContext, "group"))))))
                                        .then(Commands.literal("allow").then(Commands
                                                .argument("perm", StringArgumentType.string()).suggests(SUGGEST_perm)
                                                .executes(CommandContext -> execute(CommandContext,
                                                        "user&&" + StringArgumentType.getString(CommandContext,
                                                                "player") + "&&zone&&"
                                                                + StringArgumentType.getString(CommandContext, "zone")
                                                                + "&&allow&&"
                                                                + StringArgumentType.getString(CommandContext,
                                                                        "perm")))))
                                        .then(Commands.literal("deny").then(Commands
                                                .argument("perm", StringArgumentType.string()).suggests(SUGGEST_perm)
                                                .executes(CommandContext -> execute(CommandContext,
                                                        "user&&" + StringArgumentType.getString(CommandContext,
                                                                "player") + "&&zone&&"
                                                                + StringArgumentType.getString(CommandContext, "zone")
                                                                + "&&deny&&"
                                                                + StringArgumentType.getString(CommandContext,
                                                                        "perm")))))
                                        .then(Commands.literal("clear").then(Commands
                                                .argument("perm", StringArgumentType.string())
                                                .suggests(SUGGEST_PlayerPerm)
                                                .executes(CommandContext -> execute(CommandContext,
                                                        "user&&" + StringArgumentType.getString(CommandContext,
                                                                "player") + "&&zone&&"
                                                                + StringArgumentType.getString(CommandContext, "zone")
                                                                + "&&clear&&"
                                                                + StringArgumentType.getString(CommandContext,
                                                                        "perm")))))
                                        .then(Commands.literal("value").then(Commands
                                                .argument("perm", StringArgumentType.string()).suggests(SUGGEST_perm)
                                                .executes(CommandContext -> execute(CommandContext,
                                                        "user&&" + StringArgumentType.getString(CommandContext,
                                                                "player") + "&&zone&&"
                                                                + StringArgumentType.getString(CommandContext, "zone")
                                                                + "&&value&&"
                                                                + StringArgumentType.getString(CommandContext,
                                                                        "perm")))))
                                        .then(Commands.literal("spawn").then(Commands.literal("help")
                                                .executes(CommandContext -> execute(CommandContext,
                                                        "user&&" + StringArgumentType.getString(CommandContext,
                                                                "player") + "&&zone&&"
                                                                + StringArgumentType.getString(CommandContext, "zone")
                                                                + "&&spawn")))
                                                .then(Commands.literal("bed").then(Commands.literal("enable")
                                                        .executes(CommandContext -> execute(CommandContext,
                                                                "user&&" + StringArgumentType
                                                                        .getString(CommandContext, "player")
                                                                        + "&&zone&&"
                                                                        + StringArgumentType.getString(CommandContext,
                                                                                "zone")
                                                                        + "&&spawn&&bed&&enable")))
                                                        .then(Commands.literal("disable")
                                                                .executes(CommandContext -> execute(CommandContext,
                                                                        "user&&" + StringArgumentType
                                                                                .getString(CommandContext, "player")
                                                                                + "&&zone&&"
                                                                                + StringArgumentType.getString(
                                                                                        CommandContext, "zone")
                                                                                + "&&spawn&&bed&&disable"))))
                                                .then(Commands.literal("here")
                                                        .executes(CommandContext -> execute(CommandContext,
                                                                "user&&" + StringArgumentType
                                                                        .getString(CommandContext, "player")
                                                                        + "&&zone&&"
                                                                        + StringArgumentType.getString(CommandContext,
                                                                                "zone")
                                                                        + "&&spawn&&here")))
                                                .then(Commands.literal("clear")
                                                        .executes(CommandContext -> execute(CommandContext,
                                                                "user&&" + StringArgumentType
                                                                        .getString(CommandContext, "player")
                                                                        + "&&zone&&"
                                                                        + StringArgumentType.getString(CommandContext,
                                                                                "zone")
                                                                        + "&&spawn&&clear")))
                                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                                        .then(Commands.argument("dim", DimensionArgument.dimension())
                                                                .executes(CommandContext -> execute(CommandContext,
                                                                        "user&&" + StringArgumentType
                                                                                .getString(CommandContext, "player")
                                                                                + "&&zone&&"
                                                                                + StringArgumentType.getString(
                                                                                        CommandContext, "zone")
                                                                                + "&&spawn&&"
                                                                                + Integer.toString(BlockPosArgument
                                                                                        .getLoadedBlockPos(
                                                                                                CommandContext, "pos")
                                                                                        .getX())
                                                                                + "&&"
                                                                                + Integer.toString(BlockPosArgument
                                                                                        .getLoadedBlockPos(
                                                                                                CommandContext, "pos")
                                                                                        .getY())
                                                                                + "&&"
                                                                                + Integer.toString(BlockPosArgument
                                                                                        .getLoadedBlockPos(
                                                                                                CommandContext, "pos")
                                                                                        .getZ())
                                                                                + "&&"
                                                                                + DimensionArgument
                                                                                        .getDimension(CommandContext,
                                                                                                "dim")
                                                                                        .dimension().location()
                                                                                        .toString())))))
                                        .then(Commands.literal("prefix").then(Commands
                                                .argument("prefix", StringArgumentType.greedyString())
                                                .executes(CommandContext -> execute(CommandContext,
                                                        "user&&" + StringArgumentType.getString(CommandContext,
                                                                "player") + "&&zone&&"
                                                                + StringArgumentType.getString(CommandContext, "zone")
                                                                + "&&prefix&&"
                                                                + StringArgumentType.getString(CommandContext,
                                                                        "prefix"))))
                                                .then(Commands.literal("clear")
                                                        .executes(CommandContext -> execute(CommandContext,
                                                                "user&&" + StringArgumentType
                                                                        .getString(CommandContext, "player")
                                                                        + "&&zone&&"
                                                                        + StringArgumentType.getString(CommandContext,
                                                                                "zone")
                                                                        + "&&prefix&&clear"))))
                                        .then(Commands.literal("suffix").then(Commands
                                                .argument("suffix", StringArgumentType.greedyString())
                                                .executes(CommandContext -> execute(CommandContext,
                                                        "user&&" + StringArgumentType.getString(CommandContext,
                                                                "player") + "&&zone&&"
                                                                + StringArgumentType.getString(CommandContext, "zone")
                                                                + "&&suffix&&"
                                                                + StringArgumentType.getString(CommandContext,
                                                                        "suffix"))))
                                                .then(Commands.literal("clear")
                                                        .executes(CommandContext -> execute(CommandContext,
                                                                "user&&" + StringArgumentType
                                                                        .getString(CommandContext, "player")
                                                                        + "&&zone&&"
                                                                        + StringArgumentType.getString(CommandContext,
                                                                                "zone")
                                                                        + "&&suffix&&clear"))))
                                        .then(Commands.literal("denydefault")
                                                .executes(CommandContext -> execute(CommandContext,
                                                        "user&&" + StringArgumentType.getString(CommandContext,
                                                                "player") + "&&zone&&"
                                                                + StringArgumentType.getString(CommandContext, "zone")
                                                                + "&&denydefault"))))
                                        .then(Commands.literal("MainServerZone")
                                                .executes(CommandContext -> execute(CommandContext,
                                                        "user&&" + StringArgumentType.getString(CommandContext,
                                                                "player") + "&&zoneMain"))
                                                .then(Commands.literal("group")
                                                        .executes(CommandContext -> execute(CommandContext,
                                                                "user&&" + StringArgumentType.getString(CommandContext,
                                                                        "player") + "&&zoneMain&&group"))
                                                        .then(Commands.argument("arg", StringArgumentType.string())
                                                                .suggests(SUGGEST_parseUserGroupArgs)
                                                                .executes(CommandContext -> execute(CommandContext,
                                                                        "user&&" + StringArgumentType
                                                                                .getString(CommandContext, "player")
                                                                                + "&&zoneMain&&group&&"
                                                                                + StringArgumentType.getString(
                                                                                        CommandContext, "arg")))
                                                                .then(Commands
                                                                        .argument("group", StringArgumentType.string())
                                                                        .suggests(SUGGEST_group)
                                                                        .executes(CommandContext -> execute(
                                                                                CommandContext,
                                                                                "user&&" + StringArgumentType.getString(
                                                                                        CommandContext, "player")
                                                                                        + "&&zoneMain&&group&&"
                                                                                        + StringArgumentType.getString(
                                                                                                CommandContext, "arg")
                                                                                        + "&&"
                                                                                        + StringArgumentType.getString(
                                                                                                CommandContext,
                                                                                                "group"))))))
                                                .then(Commands.literal("allow")
                                                        .then(Commands.argument("perm", StringArgumentType.string())
                                                                .suggests(SUGGEST_perm)
                                                                .executes(CommandContext -> execute(CommandContext,
                                                                        "user&&" + StringArgumentType
                                                                                .getString(CommandContext, "player")
                                                                                + "&&zoneMain&&allow&&"
                                                                                + StringArgumentType.getString(
                                                                                        CommandContext, "perm")))))
                                                .then(Commands.literal("deny")
                                                        .then(Commands.argument("perm", StringArgumentType.string())
                                                                .suggests(SUGGEST_perm)
                                                                .executes(CommandContext -> execute(CommandContext,
                                                                        "user&&" + StringArgumentType
                                                                                .getString(CommandContext, "player")
                                                                                + "&&zoneMain&&deny&&"
                                                                                + StringArgumentType.getString(
                                                                                        CommandContext, "perm")))))
                                                .then(Commands.literal("clear")
                                                        .then(Commands.argument("perm", StringArgumentType.string())
                                                                .suggests(SUGGEST_PlayerPerm)
                                                                .executes(CommandContext -> execute(CommandContext,
                                                                        "user&&" + StringArgumentType
                                                                                .getString(CommandContext, "player")
                                                                                + "&&zoneMain&&clear&&"
                                                                                + StringArgumentType.getString(
                                                                                        CommandContext, "perm")))))
                                                .then(Commands.literal("value")
                                                        .then(Commands.argument("perm", StringArgumentType.string())
                                                                .suggests(SUGGEST_perm)
                                                                .executes(CommandContext -> execute(CommandContext,
                                                                        "user&&" + StringArgumentType
                                                                                .getString(CommandContext, "player")
                                                                                + "&&zoneMain&&value&&"
                                                                                + StringArgumentType.getString(
                                                                                        CommandContext, "perm")))))
                                                .then(Commands.literal("spawn").then(Commands.literal("help")
                                                        .executes(CommandContext -> execute(CommandContext,
                                                                "user&&" + StringArgumentType.getString(CommandContext,
                                                                        "player") + "&&zoneMain&&spawn")))
                                                        .then(Commands.literal("bed").then(Commands.literal("enable")
                                                                .executes(CommandContext -> execute(CommandContext,
                                                                        "user&&" + StringArgumentType
                                                                                .getString(CommandContext, "player")
                                                                                + "&&zoneMain&&spawn&&bed&&enable")))
                                                                .then(Commands.literal("disable")
                                                                        .executes(CommandContext -> execute(
                                                                                CommandContext,
                                                                                "user&&" + StringArgumentType.getString(
                                                                                        CommandContext, "player")
                                                                                        + "&&zoneMain&&spawn&&bed&&disable"))))
                                                        .then(Commands.literal("here")
                                                                .executes(CommandContext -> execute(CommandContext,
                                                                        "user&&" + StringArgumentType
                                                                                .getString(CommandContext, "player")
                                                                                + "&&zoneMain&&spawn&&here")))
                                                        .then(Commands.literal("clear")
                                                                .executes(CommandContext -> execute(CommandContext,
                                                                        "user&&" + StringArgumentType
                                                                                .getString(CommandContext, "player")
                                                                                + "&&zoneMain&&spawn&&clear")))
                                                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                                                .then(Commands
                                                                        .argument("dim", DimensionArgument.dimension())
                                                                        .executes(CommandContext -> execute(
                                                                                CommandContext,
                                                                                "user&&" + StringArgumentType.getString(
                                                                                        CommandContext, "player")
                                                                                        + "&&zoneMain&&spawn&&"
                                                                                        + Integer.toString(
                                                                                                BlockPosArgument
                                                                                                        .getLoadedBlockPos(
                                                                                                                CommandContext,
                                                                                                                "pos")
                                                                                                        .getX())
                                                                                        + "&&"
                                                                                        + Integer.toString(
                                                                                                BlockPosArgument
                                                                                                        .getLoadedBlockPos(
                                                                                                                CommandContext,
                                                                                                                "pos")
                                                                                                        .getY())
                                                                                        + "&&"
                                                                                        + Integer.toString(
                                                                                                BlockPosArgument
                                                                                                        .getLoadedBlockPos(
                                                                                                                CommandContext,
                                                                                                                "pos")
                                                                                                        .getZ())
                                                                                        + "&&"
                                                                                        + DimensionArgument
                                                                                                .getDimension(
                                                                                                        CommandContext,
                                                                                                        "dim")
                                                                                                .dimension().location()
                                                                                                .toString())))))
                                                .then(Commands.literal("prefix").then(Commands
                                                        .argument("prefix", StringArgumentType.greedyString())
                                                        .executes(CommandContext -> execute(CommandContext,
                                                                "user&&" + StringArgumentType
                                                                        .getString(CommandContext, "player")
                                                                        + "&&zoneMain&&prefix&&"
                                                                        + StringArgumentType.getString(CommandContext,
                                                                                "prefix"))))
                                                        .then(Commands.literal("clear")
                                                                .executes(CommandContext -> execute(CommandContext,
                                                                        "user&&" + StringArgumentType
                                                                                .getString(CommandContext, "player")
                                                                                + "&&zone&&"
                                                                                + StringArgumentType.getString(
                                                                                        CommandContext, "zone")
                                                                                + "&&prefix&&clear"))))
                                                .then(Commands.literal("suffix").then(Commands
                                                        .argument("suffix", StringArgumentType.greedyString())
                                                        .executes(CommandContext -> execute(CommandContext,
                                                                "user&&" + StringArgumentType
                                                                        .getString(CommandContext, "player")
                                                                        + "&&zoneMain&&suffix&&"
                                                                        + StringArgumentType.getString(CommandContext,
                                                                                "suffix"))))
                                                        .then(Commands.literal("clear")
                                                                .executes(CommandContext -> execute(CommandContext,
                                                                        "user&&" + StringArgumentType
                                                                                .getString(CommandContext, "player")
                                                                                + "&&zone&&"
                                                                                + StringArgumentType.getString(
                                                                                        CommandContext, "zone")
                                                                                + "&&suffix&&clear"))))
                                                .then(Commands.literal("denydefault")
                                                        .executes(CommandContext -> execute(CommandContext,
                                                                "user&&" + StringArgumentType.getString(CommandContext,
                                                                        "player") + "&&zoneMain&&denydefault")))))))
                .then(Commands.literal("group")// p
                        .executes(CommandContext -> execute(CommandContext, "group"))
                        .then(Commands.literal("help").executes(CommandContext -> execute(CommandContext, "group")))
                        .then(Commands.argument("group", StringArgumentType.string()).suggests(SUGGEST_group)
                                .then(Commands.literal("create")
                                        .executes(CommandContext -> execute(CommandContext,
                                                "group&&" + StringArgumentType.getString(CommandContext, "group")
                                                        + "&&create")))
                                .then(Commands.literal("perms")
                                        .executes(CommandContext -> execute(CommandContext,
                                                "group&&" + StringArgumentType.getString(CommandContext, "group")
                                                        + "&&perms")))
                                .then(Commands.literal("users")
                                        .executes(CommandContext -> execute(CommandContext,
                                                "group&&" + StringArgumentType.getString(CommandContext, "group")
                                                        + "&&users")))
                                .then(Commands.literal("priority").then(Commands.literal("help")
                                        .executes(CommandContext -> execute(CommandContext,
                                                "group&&" + StringArgumentType.getString(CommandContext, "group")
                                                        + "&&priority")))
                                        .then(Commands.argument("priority", IntegerArgumentType.integer())
                                                .executes(CommandContext -> execute(CommandContext,
                                                        "group&&"
                                                                + StringArgumentType.getString(CommandContext, "group")
                                                                + Integer.toString(IntegerArgumentType
                                                                        .getInteger(CommandContext, "priority"))))))
                                .then(Commands.literal("parent").then(Commands.literal("help")
                                        .executes(CommandContext -> execute(CommandContext,
                                                "group&&" + StringArgumentType.getString(CommandContext, "group")
                                                        + "&&parent")))
                                        .then(Commands.literal("add").then(Commands
                                                .argument("group1", StringArgumentType.string()).suggests(SUGGEST_group)
                                                .executes(CommandContext -> execute(CommandContext,
                                                        "group&&"
                                                                + StringArgumentType.getString(CommandContext, "group")
                                                                + "&&parent&&add&&"
                                                                + StringArgumentType.getString(CommandContext,
                                                                        "group1")))))
                                        .then(Commands.literal("remove").then(Commands
                                                .argument("group1", StringArgumentType.string()).suggests(SUGGEST_group)
                                                .executes(CommandContext -> execute(CommandContext,
                                                        "group&&"
                                                                + StringArgumentType.getString(CommandContext, "group")
                                                                + "&&parent&&remove&&"
                                                                + StringArgumentType.getString(CommandContext,
                                                                        "group1")))))
                                        .then(Commands.literal("clear")
                                                .executes(CommandContext -> execute(CommandContext,
                                                        "group&&" + StringArgumentType.getString(CommandContext,
                                                                "group") + "&&parent&&clear")))
                                        .executes(CommandContext -> execute(CommandContext,
                                                "group&&" + StringArgumentType.getString(CommandContext, "group")
                                                        + "&&parent")))
                                .then(Commands.literal("include").then(Commands.literal("help")
                                        .executes(CommandContext -> execute(CommandContext,
                                                "group&&" + StringArgumentType.getString(CommandContext, "group")
                                                        + "&&include")))
                                        .then(Commands.literal("add").then(Commands
                                                .argument("group1", StringArgumentType.string()).suggests(SUGGEST_group)
                                                .executes(CommandContext -> execute(CommandContext,
                                                        "group&&"
                                                                + StringArgumentType.getString(CommandContext, "group")
                                                                + "&&include&&add&&"
                                                                + StringArgumentType.getString(CommandContext,
                                                                        "group1")))))
                                        .then(Commands.literal("remove").then(Commands
                                                .argument("group1", StringArgumentType.string()).suggests(SUGGEST_group)
                                                .executes(CommandContext -> execute(CommandContext,
                                                        "group&&"
                                                                + StringArgumentType.getString(CommandContext, "group")
                                                                + "&&include&&remove&&"
                                                                + StringArgumentType.getString(CommandContext,
                                                                        "group1")))))
                                        .then(Commands.literal("clear")
                                                .executes(CommandContext -> execute(CommandContext,
                                                        "group&&" + StringArgumentType.getString(CommandContext,
                                                                "group") + "&&include&&clear")))
                                        .executes(CommandContext -> execute(CommandContext,
                                                "group&&" + StringArgumentType.getString(CommandContext, "group")
                                                        + "&&include")))
                                .then(Commands.literal("zone").then(Commands
                                        .argument("zone", StringArgumentType.string()).suggests(SUGGEST_zones)
                                        .executes(CommandContext -> execute(CommandContext,
                                                "group&&" + StringArgumentType.getString(CommandContext, "group")
                                                        + "&&zone&&"
                                                        + StringArgumentType.getString(CommandContext, "zone")))
                                        .then(Commands.literal("allow").then(Commands
                                                .argument("perm", StringArgumentType.string()).suggests(SUGGEST_perm)
                                                .executes(CommandContext -> execute(CommandContext, "group&&"
                                                        + StringArgumentType.getString(CommandContext, "group")
                                                        + "&&zone&&"
                                                        + StringArgumentType.getString(CommandContext, "zone")
                                                        + "&&allow&&"
                                                        + StringArgumentType.getString(CommandContext, "perm")))))
                                        .then(Commands.literal("deny").then(Commands
                                                .argument("perm", StringArgumentType.string()).suggests(SUGGEST_perm)
                                                .executes(CommandContext -> execute(CommandContext, "group&&"
                                                        + StringArgumentType.getString(CommandContext, "group")
                                                        + "&&zone&&"
                                                        + StringArgumentType.getString(CommandContext, "zone")
                                                        + "&&deny&&"
                                                        + StringArgumentType.getString(CommandContext, "perm")))))
                                        .then(Commands.literal("clear").then(Commands
                                                .argument("perm", StringArgumentType.string())
                                                .suggests(SUGGEST_GroupPerm)
                                                .executes(CommandContext -> execute(CommandContext, "group&&"
                                                        + StringArgumentType.getString(CommandContext, "group")
                                                        + "&&zone&&"
                                                        + StringArgumentType.getString(CommandContext, "zone")
                                                        + "&&clear&&"
                                                        + StringArgumentType.getString(CommandContext, "perm")))))
                                        .then(Commands.literal("value").then(Commands
                                                .argument("perm", StringArgumentType.string()).suggests(SUGGEST_perm)
                                                .executes(CommandContext -> execute(CommandContext, "group&&"
                                                        + StringArgumentType.getString(CommandContext, "group")
                                                        + "&&zone&&"
                                                        + StringArgumentType.getString(CommandContext, "zone")
                                                        + "&&value&&"
                                                        + StringArgumentType.getString(CommandContext, "perm")))))
                                        .then(Commands.literal("spawn").then(Commands.literal("help")
                                                .executes(CommandContext -> execute(CommandContext,
                                                        "group&&" + StringArgumentType.getString(CommandContext,
                                                                "group") + "&&zone&&"
                                                                + StringArgumentType.getString(CommandContext, "zone")
                                                                + "&&spawn")))
                                                .then(Commands.literal("bed").then(Commands.literal("enable")
                                                        .executes(CommandContext -> execute(CommandContext,
                                                                "group&&"
                                                                        + StringArgumentType
                                                                                .getString(CommandContext, "group")
                                                                        + "&&zone&&"
                                                                        + StringArgumentType.getString(CommandContext,
                                                                                "zone")
                                                                        + "&&spawn&&bed&&enable")))
                                                        .then(Commands.literal("disable").executes(
                                                                CommandContext -> execute(CommandContext, "group&&"
                                                                        + StringArgumentType
                                                                                .getString(CommandContext, "group")
                                                                        + "&&zone&&"
                                                                        + StringArgumentType.getString(CommandContext,
                                                                                "zone")
                                                                        + "&&spawn&&bed&&disable"))))
                                                .then(Commands.literal("here")
                                                        .executes(CommandContext -> execute(CommandContext, "group&&"
                                                                + StringArgumentType.getString(CommandContext, "group")
                                                                + "&&zone&&"
                                                                + StringArgumentType.getString(CommandContext, "zone")
                                                                + "&&spawn&&here")))
                                                .then(Commands.literal("clear")
                                                        .executes(CommandContext -> execute(CommandContext, "group&&"
                                                                + StringArgumentType.getString(CommandContext, "group")
                                                                + "&&zone&&"
                                                                + StringArgumentType.getString(CommandContext, "zone")
                                                                + "&&spawn&&clear")))
                                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                                        .then(Commands.argument("dim", DimensionArgument.dimension())
                                                                .executes(CommandContext -> execute(CommandContext,
                                                                        "group&&"
                                                                                + StringArgumentType.getString(
                                                                                        CommandContext, "group")
                                                                                + "&&zone&&"
                                                                                + StringArgumentType.getString(
                                                                                        CommandContext, "zone")
                                                                                + "&&spawn&&"
                                                                                + Integer.toString(BlockPosArgument
                                                                                        .getLoadedBlockPos(
                                                                                                CommandContext, "pos")
                                                                                        .getX())
                                                                                + "&&"
                                                                                + Integer.toString(BlockPosArgument
                                                                                        .getLoadedBlockPos(
                                                                                                CommandContext, "pos")
                                                                                        .getY())
                                                                                + "&&"
                                                                                + Integer.toString(BlockPosArgument
                                                                                        .getLoadedBlockPos(
                                                                                                CommandContext, "pos")
                                                                                        .getZ())
                                                                                + "&&"
                                                                                + DimensionArgument
                                                                                        .getDimension(CommandContext,
                                                                                                "dim")
                                                                                        .dimension().location()
                                                                                        .toString())))))
                                        .then(Commands.literal("prefix").then(Commands
                                                .argument("prefix", StringArgumentType.greedyString())
                                                .executes(CommandContext -> execute(CommandContext, "group&&"
                                                        + StringArgumentType.getString(CommandContext, "group")
                                                        + "&&zone&&"
                                                        + StringArgumentType.getString(CommandContext, "zone")
                                                        + "&&prefix&&"
                                                        + StringArgumentType.getString(CommandContext, "prefix"))))
                                                .then(Commands.literal("clear")
                                                        .executes(CommandContext -> execute(CommandContext, "group&&"
                                                                + StringArgumentType.getString(CommandContext, "group")
                                                                + "&&zone&&"
                                                                + StringArgumentType.getString(CommandContext, "zone")
                                                                + "&&prefix&&clear"))))
                                        .then(Commands.literal("suffix").then(Commands
                                                .argument("suffix", StringArgumentType.greedyString())
                                                .executes(CommandContext -> execute(CommandContext, "group&&"
                                                        + StringArgumentType.getString(CommandContext, "group")
                                                        + "&&zone&&"
                                                        + StringArgumentType.getString(CommandContext, "zone")
                                                        + "&&suffix&&"
                                                        + StringArgumentType.getString(CommandContext, "suffix"))))
                                                .then(Commands.literal("clear")
                                                        .executes(CommandContext -> execute(CommandContext, "group&&"
                                                                + StringArgumentType.getString(CommandContext, "group")
                                                                + "&&zone&&"
                                                                + StringArgumentType.getString(CommandContext, "zone")
                                                                + "&&suffix&&clear"))))
                                        .then(Commands.literal("denydefault")
                                                .executes(CommandContext -> execute(CommandContext,
                                                        "group&&" + StringArgumentType.getString(CommandContext,
                                                                "group") + "&&zone&&"
                                                                + StringArgumentType.getString(CommandContext, "zone")
                                                                + "&&denydefault"))))
                                        .then(Commands.literal("MainServerZone").executes(CommandContext -> execute(
                                                CommandContext,
                                                "group&&" + StringArgumentType.getString(CommandContext, "group")
                                                        + "&&zoneMain"))
                                                .then(Commands.literal("allow").then(Commands
                                                        .argument("perm", StringArgumentType.string())
                                                        .suggests(SUGGEST_perm)
                                                        .executes(CommandContext -> execute(CommandContext,
                                                                "group&&"
                                                                        + StringArgumentType
                                                                                .getString(CommandContext, "group")
                                                                        + "&&zoneMain&&allow&&"
                                                                        + StringArgumentType.getString(CommandContext,
                                                                                "perm")))))
                                                .then(Commands.literal("deny").then(Commands
                                                        .argument("perm", StringArgumentType.string())
                                                        .suggests(SUGGEST_perm)
                                                        .executes(CommandContext -> execute(CommandContext,
                                                                "group&&"
                                                                        + StringArgumentType
                                                                                .getString(CommandContext, "group")
                                                                        + "&&zoneMain&&deny&&"
                                                                        + StringArgumentType.getString(CommandContext,
                                                                                "perm")))))
                                                .then(Commands.literal("clear").then(Commands
                                                        .argument("perm", StringArgumentType.string())
                                                        .suggests(SUGGEST_GroupPerm).executes(
                                                                CommandContext -> execute(CommandContext,
                                                                        "group&&" + StringArgumentType
                                                                                .getString(CommandContext, "group")
                                                                                + "&&zoneMain&&clear&&"
                                                                                + StringArgumentType.getString(
                                                                                        CommandContext, "perm")))))
                                                .then(Commands.literal("value").then(Commands
                                                        .argument("perm", StringArgumentType.string())
                                                        .suggests(SUGGEST_perm)
                                                        .executes(CommandContext -> execute(CommandContext,
                                                                "group&&"
                                                                        + StringArgumentType
                                                                                .getString(CommandContext, "group")
                                                                        + "&&zoneMain&&value&&"
                                                                        + StringArgumentType.getString(CommandContext,
                                                                                "perm")))))
                                                .then(Commands.literal("spawn").then(Commands.literal("help")
                                                        .executes(CommandContext -> execute(CommandContext,
                                                                "group&&" + StringArgumentType.getString(CommandContext,
                                                                        "group") + "&&zoneMain&&spawn")))
                                                        .then(Commands.literal("bed").then(Commands.literal("enable")
                                                                .executes(CommandContext -> execute(CommandContext,
                                                                        "group&&"
                                                                                + StringArgumentType.getString(
                                                                                        CommandContext, "group")
                                                                                + "&&zoneMain&&spawn&&bed&&enable")))
                                                                .then(Commands.literal("disable")
                                                                        .executes(CommandContext -> execute(
                                                                                CommandContext,
                                                                                "group&&"
                                                                                        + StringArgumentType.getString(
                                                                                                CommandContext, "group")
                                                                                        + "&&zoneMain&&spawn&&bed&&disable"))))
                                                        .then(Commands.literal("here")
                                                                .executes(CommandContext -> execute(CommandContext,
                                                                        "group&&"
                                                                                + StringArgumentType.getString(
                                                                                        CommandContext, "group")
                                                                                + "&&zoneMain&&spawn&&here")))
                                                        .then(Commands.literal("clear")
                                                                .executes(CommandContext -> execute(CommandContext,
                                                                        "group&&"
                                                                                + StringArgumentType.getString(
                                                                                        CommandContext, "group")
                                                                                + "&&zoneMain&&spawn&&clear")))
                                                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                                                .then(Commands
                                                                        .argument("dim", DimensionArgument.dimension())
                                                                        .executes(CommandContext -> execute(
                                                                                CommandContext,
                                                                                "group&&"
                                                                                        + StringArgumentType.getString(
                                                                                                CommandContext, "group")
                                                                                        + "&&zoneMain&&spawn&&"
                                                                                        + Integer.toString(
                                                                                                BlockPosArgument
                                                                                                        .getLoadedBlockPos(
                                                                                                                CommandContext,
                                                                                                                "pos")
                                                                                                        .getX())
                                                                                        + "&&"
                                                                                        + Integer.toString(
                                                                                                BlockPosArgument
                                                                                                        .getLoadedBlockPos(
                                                                                                                CommandContext,
                                                                                                                "pos")
                                                                                                        .getY())
                                                                                        + "&&"
                                                                                        + Integer.toString(
                                                                                                BlockPosArgument
                                                                                                        .getLoadedBlockPos(
                                                                                                                CommandContext,
                                                                                                                "pos")
                                                                                                        .getZ())
                                                                                        + "&&"
                                                                                        + DimensionArgument
                                                                                                .getDimension(
                                                                                                        CommandContext,
                                                                                                        "dim")
                                                                                                .dimension().location()
                                                                                                .toString())))))
                                                .then(Commands.literal("prefix").then(Commands
                                                        .argument("prefix", StringArgumentType.greedyString())
                                                        .executes(CommandContext -> execute(CommandContext,
                                                                "group&&"
                                                                        + StringArgumentType
                                                                                .getString(CommandContext, "group")
                                                                        + "&&zoneMain&&prefix&&"
                                                                        + StringArgumentType.getString(CommandContext,
                                                                                "prefix"))))
                                                        .then(Commands.literal("clear")
                                                                .executes(CommandContext -> execute(CommandContext,
                                                                        "group&&"
                                                                                + StringArgumentType.getString(
                                                                                        CommandContext, "group")
                                                                                + "&&zoneMain&&prefix&&clear"))))
                                                .then(Commands.literal("suffix").then(Commands
                                                        .argument("suffix", StringArgumentType.greedyString())
                                                        .executes(CommandContext -> execute(CommandContext,
                                                                "group&&"
                                                                        + StringArgumentType
                                                                                .getString(CommandContext, "group")
                                                                        + "&&zoneMain&&suffix&&"
                                                                        + StringArgumentType.getString(CommandContext,
                                                                                "suffix"))))
                                                        .then(Commands.literal("clear")
                                                                .executes(CommandContext -> execute(CommandContext,
                                                                        "group&&"
                                                                                + StringArgumentType.getString(
                                                                                        CommandContext, "group")
                                                                                + "&&zoneMain&&suffix&&clear"))))
                                                .then(Commands.literal("denydefault")
                                                        .executes(CommandContext -> execute(CommandContext,
                                                                "group&&"
                                                                        + StringArgumentType.getString(CommandContext,
                                                                                "group")
                                                                        + "&&zoneMain&&denydefault")))))))
                .then(Commands.literal("global")// p
                        .then(Commands.literal("perms")
                                .executes(CommandContext -> execute(CommandContext, "global&&perms")))
                        .then(Commands.literal("users")
                                .executes(CommandContext -> execute(CommandContext, "global&&users")))
                        .then(Commands.literal("priority")
                                .then(Commands.literal("help")
                                        .executes(CommandContext -> execute(CommandContext, "global&&priority")))
                                .then(Commands.argument("priority", IntegerArgumentType.integer())
                                        .executes(CommandContext -> execute(CommandContext,
                                                "global&&" + Integer.toString(
                                                        IntegerArgumentType.getInteger(CommandContext, "priority"))))))
                        .then(Commands.literal("parent")
                                .then(Commands.literal("help")
                                        .executes(CommandContext -> execute(CommandContext, "global&&parent")))
                                .then(Commands.literal("add").then(Commands
                                        .argument("group1", StringArgumentType.string()).suggests(SUGGEST_group)
                                        .executes(CommandContext -> execute(CommandContext,
                                                "global&&parent&&add&&"
                                                        + StringArgumentType.getString(CommandContext, "group1")))))
                                .then(Commands.literal("remove").then(Commands
                                        .argument("group1", StringArgumentType.string()).suggests(SUGGEST_group)
                                        .executes(CommandContext -> execute(CommandContext,
                                                "global&&parent&&remove&&"
                                                        + StringArgumentType.getString(CommandContext, "group1")))))
                                .then(Commands.literal("clear")
                                        .executes(CommandContext -> execute(CommandContext, "global&&parent&&clear")))
                                .executes(CommandContext -> execute(CommandContext, "global&&parent")))
                        .then(Commands.literal("include")
                                .then(Commands.literal("help")
                                        .executes(CommandContext -> execute(CommandContext, "global&&include")))
                                .then(Commands.literal("add").then(Commands
                                        .argument("group1", StringArgumentType.string()).suggests(SUGGEST_group)
                                        .executes(CommandContext -> execute(CommandContext,
                                                "global&&include&&add&&"
                                                        + StringArgumentType.getString(CommandContext, "group1")))))
                                .then(Commands.literal("remove").then(Commands
                                        .argument("group1", StringArgumentType.string()).suggests(SUGGEST_group)
                                        .executes(CommandContext -> execute(CommandContext,
                                                "global&&include&&remove&&"
                                                        + StringArgumentType.getString(CommandContext, "group1")))))
                                .then(Commands.literal("clear")
                                        .executes(CommandContext -> execute(CommandContext, "global&&include&&clear")))
                                .executes(CommandContext -> execute(CommandContext, "global&&include")))
                        .then(Commands.literal("zone").then(Commands.argument("zone", StringArgumentType.string())
                                .suggests(SUGGEST_zones)
                                .then(Commands.literal("allow").then(Commands
                                        .argument("perm", StringArgumentType.string()).suggests(SUGGEST_perm)
                                        .executes(CommandContext -> execute(CommandContext,
                                                "global&&zone&&" + StringArgumentType.getString(CommandContext, "zone")
                                                        + "&&allow&&"
                                                        + StringArgumentType.getString(CommandContext, "perm")))))
                                .then(Commands.literal("deny").then(Commands
                                        .argument("perm", StringArgumentType.string()).suggests(SUGGEST_perm)
                                        .executes(CommandContext -> execute(CommandContext,
                                                "global&&zone&&" + StringArgumentType.getString(CommandContext, "zone")
                                                        + "&&deny&&"
                                                        + StringArgumentType.getString(CommandContext, "perm")))))
                                .then(Commands.literal("clear").then(Commands
                                        .argument("perm", StringArgumentType.string()).suggests(SUGGEST_GlobalPerm)
                                        .executes(CommandContext -> execute(CommandContext,
                                                "global&&zone&&" + StringArgumentType.getString(CommandContext, "zone")
                                                        + "&&clear&&"
                                                        + StringArgumentType.getString(CommandContext, "perm")))))
                                .then(Commands.literal("value").then(Commands
                                        .argument("perm", StringArgumentType.string()).suggests(SUGGEST_perm)
                                        .executes(CommandContext -> execute(CommandContext,
                                                "global&&zone&&" + StringArgumentType.getString(CommandContext, "zone")
                                                        + "&&value&&"
                                                        + StringArgumentType.getString(CommandContext, "perm")))))
                                .then(Commands.literal("spawn").then(Commands.literal("help")
                                        .executes(CommandContext -> execute(CommandContext,
                                                "global&&zone&&" + StringArgumentType.getString(CommandContext, "zone")
                                                        + "&&spawn")))
                                        .then(Commands.literal("bed").then(Commands.literal("enable")
                                                .executes(CommandContext -> execute(CommandContext,
                                                        "global&&zone&&"
                                                                + StringArgumentType.getString(CommandContext, "zone")
                                                                + "&&spawn&&bed&&enable")))
                                                .then(Commands.literal("disable")
                                                        .executes(CommandContext -> execute(CommandContext,
                                                                "global&&zone&&" + StringArgumentType
                                                                        .getString(CommandContext, "zone")
                                                                        + "&&spawn&&bed&&disable"))))
                                        .then(Commands.literal("here").executes(CommandContext -> execute(
                                                CommandContext,
                                                "global&&zone&&" + StringArgumentType.getString(CommandContext, "zone")
                                                        + "&&spawn&&here")))
                                        .then(Commands.literal("clear").executes(CommandContext -> execute(
                                                CommandContext,
                                                "global&&zone&&" + StringArgumentType.getString(CommandContext, "zone")
                                                        + "&&spawn&&clear")))
                                        .then(Commands.argument("pos", BlockPosArgument.blockPos()).then(Commands
                                                .argument("dim", DimensionArgument.dimension())
                                                .executes(CommandContext -> execute(CommandContext, "global&&zone&&"
                                                        + StringArgumentType.getString(CommandContext, "zone")
                                                        + "&&spawn&&"
                                                        + Integer.toString(BlockPosArgument
                                                                .getLoadedBlockPos(CommandContext, "pos").getX())
                                                        + "&&"
                                                        + Integer.toString(BlockPosArgument
                                                                .getLoadedBlockPos(CommandContext, "pos").getY())
                                                        + "&&"
                                                        + Integer.toString(BlockPosArgument
                                                                .getLoadedBlockPos(CommandContext, "pos").getZ())
                                                        + "&&"
                                                        + DimensionArgument.getDimension(CommandContext, "dim")
                                                                .dimension().location().toString())))))
                                .then(Commands.literal("prefix").then(Commands
                                        .argument("prefix", StringArgumentType.greedyString())
                                        .executes(CommandContext -> execute(CommandContext,
                                                "global&&zone&&" + StringArgumentType.getString(CommandContext, "zone")
                                                        + "&&prefix&&"
                                                        + StringArgumentType.getString(CommandContext, "prefix"))))
                                        .then(Commands.literal("clear").executes(CommandContext -> execute(
                                                CommandContext,
                                                "global&&zone&&" + StringArgumentType.getString(CommandContext, "zone")
                                                        + "&&prefix&&clear"))))
                                .then(Commands.literal("suffix").then(Commands
                                        .argument("suffix", StringArgumentType.greedyString())
                                        .executes(CommandContext -> execute(CommandContext,
                                                "global&&zone&&" + StringArgumentType.getString(CommandContext, "zone")
                                                        + "&&suffix&&"
                                                        + StringArgumentType.getString(CommandContext, "suffix"))))
                                        .then(Commands.literal("clear").executes(CommandContext -> execute(
                                                CommandContext,
                                                "global&&zone&&" + StringArgumentType.getString(CommandContext, "zone")
                                                        + "&&suffix&&clear"))))
                                .then(Commands.literal("denydefault")
                                        .executes(CommandContext -> execute(CommandContext,
                                                "global&&zone&&" + StringArgumentType.getString(CommandContext, "zone")
                                                        + "&&denydefault"))))
                                .then(Commands.literal("MainServerZone").then(Commands.literal("allow").then(Commands
                                        .argument("perm", StringArgumentType.string()).suggests(SUGGEST_perm)
                                        .executes(CommandContext -> execute(CommandContext,
                                                "global&&zoneMain&&allow&&"
                                                        + StringArgumentType.getString(CommandContext, "perm")))))
                                        .then(Commands.literal("deny")
                                                .then(Commands.argument("perm", StringArgumentType.string())
                                                        .suggests(SUGGEST_perm)
                                                        .executes(CommandContext -> execute(CommandContext,
                                                                "global&&zoneMain&&deny&&" + StringArgumentType
                                                                        .getString(CommandContext, "perm")))))
                                        .then(Commands.literal("clear")
                                                .then(Commands.argument("perm", StringArgumentType.string())
                                                        .suggests(SUGGEST_GlobalPerm)
                                                        .executes(CommandContext -> execute(CommandContext,
                                                                "global&&zoneMain&&clear&&" + StringArgumentType
                                                                        .getString(CommandContext, "perm")))))
                                        .then(Commands.literal("value")
                                                .then(Commands.argument("perm", StringArgumentType.string())
                                                        .suggests(SUGGEST_perm)
                                                        .executes(CommandContext -> execute(CommandContext,
                                                                "global&&zoneMain&&value&&" + StringArgumentType
                                                                        .getString(CommandContext, "perm")))))
                                        .then(Commands.literal("spawn").then(Commands.literal("help").executes(
                                                CommandContext -> execute(CommandContext, "global&&zoneMain&&spawn")))
                                                .then(Commands.literal("bed")
                                                        .then(Commands.literal("enable")
                                                                .executes(CommandContext -> execute(CommandContext,
                                                                        "global&&zoneMain&&spawn&&bed&&enable")))
                                                        .then(Commands.literal("disable")
                                                                .executes(CommandContext -> execute(CommandContext,
                                                                        "global&&zoneMain&&spawn&&bed&&disable"))))
                                                .then(Commands.literal("here")
                                                        .executes(CommandContext -> execute(CommandContext,
                                                                "global&&zoneMain&&spawn&&here")))
                                                .then(Commands.literal("clear")
                                                        .executes(CommandContext -> execute(CommandContext,
                                                                "global&&zoneMain&&spawn&&clear")))
                                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                                        .then(Commands.argument("dim", DimensionArgument.dimension())
                                                                .executes(CommandContext -> execute(CommandContext,
                                                                        "global&&zoneMain&&spawn&&"
                                                                                + Integer.toString(BlockPosArgument
                                                                                        .getLoadedBlockPos(
                                                                                                CommandContext, "pos")
                                                                                        .getX())
                                                                                + "&&"
                                                                                + Integer.toString(BlockPosArgument
                                                                                        .getLoadedBlockPos(
                                                                                                CommandContext, "pos")
                                                                                        .getY())
                                                                                + "&&"
                                                                                + Integer.toString(BlockPosArgument
                                                                                        .getLoadedBlockPos(
                                                                                                CommandContext, "pos")
                                                                                        .getZ())
                                                                                + "&&"
                                                                                + DimensionArgument
                                                                                        .getDimension(CommandContext,
                                                                                                "dim")
                                                                                        .dimension().location()
                                                                                        .toString())))))
                                        .then(Commands.literal("prefix")
                                                .then(Commands.argument("prefix", StringArgumentType.greedyString())
                                                        .executes(CommandContext -> execute(CommandContext,
                                                                "global&&zoneMain&&prefix&&" + StringArgumentType
                                                                        .getString(CommandContext, "prefix"))))
                                                .then(Commands.literal("clear")
                                                        .executes(CommandContext -> execute(CommandContext,
                                                                "global&&zoneMain&&prefix&&clear"))))
                                        .then(Commands.literal("suffix")
                                                .then(Commands.argument("suffix", StringArgumentType.greedyString())
                                                        .executes(CommandContext -> execute(CommandContext,
                                                                "global&&zoneMain&&suffix&&" + StringArgumentType
                                                                        .getString(CommandContext, "suffix"))))
                                                .then(Commands.literal("clear")
                                                        .executes(CommandContext -> execute(CommandContext,
                                                                "global&&zoneMain&&suffix&&clear"))))
                                        .then(Commands.literal("denydefault")
                                                .executes(CommandContext -> execute(CommandContext,
                                                        "global&&zoneMain&&denydefault"))))))
                .then(Commands.literal("list")// p
                        .executes(CommandContext -> execute(CommandContext, "list"))
                        .then(Commands.literal("help").executes(CommandContext -> execute(CommandContext, "list")))
                        .then(Commands.argument("type", StringArgumentType.string()).suggests(SUGGEST_ListArgs)
                                .executes(CommandContext -> execute(CommandContext,
                                        "list&&" + StringArgumentType.getString(CommandContext, "type")))))
                .then(Commands.literal("test")// p
                        .then(Commands.argument("perm", StringArgumentType.string()).suggests(SUGGEST_perm)
                                .executes(CommandContext -> execute(CommandContext,
                                        "test&&" + StringArgumentType.getString(CommandContext, "perm")))))
                .then(Commands.literal("reload")// p
                        .executes(CommandContext -> execute(CommandContext, "reload")))
                .then(Commands.literal("save")// p
                        .then(Commands.literal("help").executes(CommandContext -> execute(CommandContext, "save")))
                        .then(Commands.literal("disable")
                                .executes(CommandContext -> execute(CommandContext, "save&&disable")))
                        .then(Commands.literal("enable")
                                .executes(CommandContext -> execute(CommandContext, "save&&enable")))
                        .then(Commands.literal("flatfile")
                                .executes(CommandContext -> execute(CommandContext, "save&&flatfile")))
                        .then(Commands.literal("singlejson")
                                .executes(CommandContext -> execute(CommandContext, "save&&singlejson")))
                        .then(Commands.literal("json").executes(CommandContext -> execute(CommandContext, "savejson"))))
                .then(Commands.literal("debug")// p
                        .executes(CommandContext -> execute(CommandContext, "debug")));
    }

    public static final SuggestionProvider<CommandSource> SUGGEST_ListArgs = (ctx, builder) -> {
        List<String> listArgs = new ArrayList<>();
        for (String arg : PermissionCommandParser.parseListArgs)
        {
            listArgs.add(arg);
        }
        return ISuggestionProvider.suggest(listArgs, builder);
    };
    public static final SuggestionProvider<CommandSource> SUGGEST_parseUserGroupArgs = (ctx, builder) -> {
        List<String> listArgs = new ArrayList<>();
        for (String arg : PermissionCommandParser.parseUserGroupArgs)
        {
            listArgs.add(arg);
        }
        return ISuggestionProvider.suggest(listArgs, builder);
    };
    public static final SuggestionProvider<CommandSource> SUGGEST_zones = (ctx, builder) -> {
        List<String> listzones = new ArrayList<>();
        for (Zone z : APIRegistry.perms.getZones())
        {
            listzones.add(z.getName());
        }
        for (String n : APIRegistry.namedWorldHandler.getShortWorldNames())
        {
            listzones.add(n);
        }
        return ISuggestionProvider.suggest(listzones, builder);
    };
    public static final SuggestionProvider<CommandSource> SUGGEST_group = (ctx, builder) -> {
        List<String> listgroup = new ArrayList<>();
        for (String z : APIRegistry.perms.getServerZone().getGroups())
        {
            listgroup.add(z);
        }
        return ISuggestionProvider.suggest(listgroup, builder);
    };
    public static final SuggestionProvider<CommandSource> SUGGEST_perm = (ctx, builder) -> {
        List<String> listperm = new ArrayList<>();
        for (String z : APIRegistry.perms.getServerZone().getRootZone().enumRegisteredPermissions())
        {
            listperm.add(z);
        }
        for (int index = 0; index < listperm.size(); index++)
        {
            if (listperm.get(index).contains("*"))
            {
                listperm.set(index, listperm.get(index).replace("*", "+"));
            }
        }
        return ISuggestionProvider.suggest(listperm, builder);
    };
    public static final SuggestionProvider<CommandSource> SUGGEST_GroupPerm = (ctx, builder) -> {
        List<String> listclear = new ArrayList<>();
        Zone zone;
        try
        {
            zone = PermissionCommandParser.parseZoneSafe(ctx.getSource(), StringArgumentType.getString(ctx, "zone"));
        }
        catch (IllegalArgumentException e)
        {
            zone = APIRegistry.perms.getServerZone();
        }
        for (String z : zone.getGroupPermissions(StringArgumentType.getString(ctx, "group")).keySet())
        {
            listclear.add(z);
        }
        return ISuggestionProvider.suggest(listclear, builder);
    };
    public static final SuggestionProvider<CommandSource> SUGGEST_GlobalPerm = (ctx, builder) -> {
        List<String> listclear = new ArrayList<>();
        Zone zone;
        try
        {
            zone = PermissionCommandParser.parseZoneSafe(ctx.getSource(), StringArgumentType.getString(ctx, "zone"));
        }
        catch (IllegalArgumentException e)
        {
            zone = APIRegistry.perms.getServerZone();
        }
        for (String z : zone.getGroupPermissions(Zone.GROUP_DEFAULT).keySet())
        {
            listclear.add(z);
        }
        return ISuggestionProvider.suggest(listclear, builder);
    };
    public static final SuggestionProvider<CommandSource> SUGGEST_PlayerPerm = (ctx, builder) -> {
        List<String> listclear = new ArrayList<>();
        Zone zone;
        try
        {
            zone = PermissionCommandParser.parseZoneSafe(ctx.getSource(), StringArgumentType.getString(ctx, "zone"));
        }
        catch (IllegalArgumentException e)
        {
            zone = APIRegistry.perms.getServerZone();
        }
        try
        {
            UserIdent ident = parsePlayer(StringArgumentType.getString(ctx, "player"), null, false, false);
            for (String z : zone.getPlayerPermissions(ident).keySet())
            {
                listclear.add(z);
            }
        }
        catch (FECommandParsingException e)
        {
        }
        return ISuggestionProvider.suggest(listclear, builder);
    };

    @Override
    public int execute(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("help"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    "/feperm " + StringUtils.join(PermissionCommandParser.parseMainArgs, "|")
                            + ": Displays help for the subcommands");
            return Command.SINGLE_SUCCESS;
        }
        List<String> args = new ArrayList<String>(Arrays.asList(params.split("&&")));
        PermissionCommandParser.parseMain(ctx, args);
        return Command.SINGLE_SUCCESS;
    }

}
