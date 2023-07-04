package com.forgeessentials.commands.item;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

public class CommandRepair extends ForgeEssentialsCommandBuilder {

	public CommandRepair(boolean enabled) {
		super(enabled);
	}

	@Override
	public String getPrimaryAlias() {
		return "repair";
	}

	@Override
	public boolean canConsoleUseCommand() {
		return true;
	}

	@Override
	public DefaultPermissionLevel getPermissionLevel() {
		return DefaultPermissionLevel.OP;
	}

	@Override
	public String getPermissionNode() {
		return ModuleCommands.PERM + ".repair";
	}

	@Override
	public void registerExtraPermissions() {
		APIRegistry.perms.registerPermission(getPermissionNode() + ".others", DefaultPermissionLevel.OP,
				"Allows repairing items held by another player");
	}

	@Override
	public LiteralArgumentBuilder<CommandSource> setExecution() {
		return baseBuilder
				.then(Commands.literal("self")
						.then(Commands.literal("Custom")
								.then(Commands.argument("amount", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
										.executes(CommandContext -> execute(CommandContext, "Custom-Self"))))
						.then(Commands.literal("MaxValue")
								.executes(CommandContext -> execute(CommandContext, "Max-Self"))))
				.then(Commands.literal("others")
						.then(Commands.argument("player", EntityArgument.player()).then(Commands.literal("Custom")
								.then(Commands.argument("amount", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
										.executes(CommandContext -> execute(CommandContext, "Custom-Others"))))

								.then(Commands.literal("MaxValue")
										.executes(CommandContext -> execute(CommandContext, "Max-Others")))));
	}

	@Override
	public int processCommandPlayer(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException {
		String[] args = params.split("-");
		if (args[1].equals("self")) {
			if (args[0].equals("Max")) {
				ItemStack item = getServerPlayer(ctx.getSource()).getMainHandItem();
				if (item == null) {
					ChatOutputHandler.chatError(ctx.getSource(), "You are not holding a reparable item.");
					return Command.SINGLE_SUCCESS;
				}
				item.setDamageValue(0);
			} else if (args[0].equals("Custom")) {
				ItemStack item = getServerPlayer(ctx.getSource()).getMainHandItem();
				if (item == null) {
					ChatOutputHandler.chatError(ctx.getSource(), "You are not holding a reparable item.");
					return Command.SINGLE_SUCCESS;
				}
				item.setDamageValue(IntegerArgumentType.getInteger(ctx, "amount"));
			}
		} else if (args[1].equals("others")
				&& hasPermission(getServerPlayer(ctx.getSource()).createCommandSourceStack(), getPermissionNode() + ".others")) {
			ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
			if (args[0].equals("Max")) {
				ItemStack item = player.getMainHandItem();
				if (item != null)
					item.setDamageValue(0);
			} else if (args[0].equals("Custom")) {
				ItemStack item = player.getMainHandItem();
				if (item != null)
					item.setDamageValue(IntegerArgumentType.getInteger(ctx, "amount"));
			}
		}
		return Command.SINGLE_SUCCESS;

	}

	@Override
	public int processCommandConsole(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException {
		String[] args = params.split("-");
		if (args[1].equals("others")) {
			ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
			if (args[0].equals("Max")) {
				ItemStack item = player.getMainHandItem();
				if (item != null)
					item.setDamageValue(0);
			} else if (args[0].equals("Custom")) {
				ItemStack item = player.getMainHandItem();
				if (item != null)
					item.setDamageValue(IntegerArgumentType.getInteger(ctx, "amount"));
			}
		} else // params[1].toString() =="self"
		{
			{
				ChatOutputHandler.chatError(ctx.getSource(), "You must select a player!");
				return Command.SINGLE_SUCCESS;
			}

		}
		return Command.SINGLE_SUCCESS;
	}
}
