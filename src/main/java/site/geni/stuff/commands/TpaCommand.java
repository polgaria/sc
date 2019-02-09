package site.geni.stuff.commands;

import com.google.common.collect.HashBiMap;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.events.ServerEvent;
import net.minecraft.command.CommandException;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TpaCommand {
	private static HashBiMap<UUID, UUID> TP = HashBiMap.create();

	public static void register() {
		/* register tpa command */
		ServerEvent.START.register(
				server -> server.getCommandManager().getDispatcher().register(
						ServerCommandManager.literal("tpa").then(
								ServerCommandManager.argument(
										"player", EntityArgumentType.onePlayer()
								).executes(
										commandContext -> onTpaCommand(commandContext, commandContext.getSource().getPlayer(), EntityArgumentType.method_9315(commandContext, "player"))
								)
						)
				)
		);

		/* register tpaccept command */
		ServerEvent.START.register(
				server -> server.getCommandManager().getDispatcher().register(
						ServerCommandManager.literal("tpaccept").executes(
								commandContext -> onTpAcceptCommand(commandContext, commandContext.getSource().getPlayer())
						)
				)
		);

		/* register tpadeny command */
		ServerEvent.START.register(
				server -> server.getCommandManager().getDispatcher().register(
						ServerCommandManager.literal("tpadeny").executes(
								commandContext -> onTpDenyCommand(commandContext, commandContext.getSource().getPlayer())
						)
				)
		);
	}

	private static int onTpaCommand(CommandContext<ServerCommandSource> commandContext, ServerPlayerEntity originPlayer, ServerPlayerEntity destPlayer) throws CommandSyntaxException {
		if (TP.containsKey(destPlayer.getUuid())) {
			throw new CommandException(new StringTextComponent("This player already has a pending TPA request!"));
		} else if (TP.containsValue(originPlayer.getUuid())) {
			throw new CommandException(new StringTextComponent("You've already sent a TPA request!"));
		}

		/* add players to requests */
		if (!originPlayer.equals(destPlayer)) {
			TP.put(destPlayer.getUuid(), originPlayer.getUuid());
		} else {
			throw new CommandException(new StringTextComponent("You can't teleport to yourself!"));
		}

		/* send message confirming to origin player that the request has been sent */
		final TextComponent sentTpaRequest = new StringTextComponent(String.format("\u00a76TPA request to \u00a74%s\u00a76 sent.", destPlayer.getDisplayName().getText()));
		commandContext.getSource().sendFeedback(sentTpaRequest, false);

		/* send message to destination player alerting them of the TPA request */
		final TextComponent receivedTpaRequest = new StringTextComponent(String.format("\u00a74%s \u00a76has sent you a TPA request! \u00a76Accept with \u00a72/tpaccept\u00a76 or deny with \u00a72/tpdeny\u00a76.", originPlayer.getDisplayName().getText()));
		final TextComponent tpaRequestExpiresIn = new StringTextComponent("\u00a76You have 30 seconds to respond until the TPA request \u00a76expires.");
		destPlayer.addChatMessage(receivedTpaRequest, false);
		destPlayer.addChatMessage(tpaRequestExpiresIn, false);

		/* schedule the executor to expire the request after 30 seconds */
		final Runnable expire = () -> {
			if (TP.containsKey(destPlayer.getUuid())) {
				/* remove expired TPA request from requests */
				TP.remove(destPlayer.getUuid());

				/* send messages to both players alerting them that the TPA request has expired */
				final TextComponent requestExpiredFromMessage = new StringTextComponent(String.format("\u00a7cTPA request from \u00a74%s\u00a7c expired.", originPlayer.getDisplayName().getText()));
				final TextComponent requestExpiredToMessage = new StringTextComponent(String.format("\u00a7cTPA request to \u00a74%s\u00a7c expired.", destPlayer.getDisplayName().getText()));

				commandContext.getSource().sendFeedback(requestExpiredToMessage, false);
				destPlayer.addChatMessage(requestExpiredFromMessage, false);
			}
		};

		Executors.newScheduledThreadPool(1).schedule(expire, 30, TimeUnit.SECONDS);

		return 1;
	}

	private static int onTpAcceptCommand(CommandContext<ServerCommandSource> commandContext, ServerPlayerEntity player) throws CommandSyntaxException {
		ServerPlayerEntity originPlayer = null;
		final List<ServerPlayerEntity> playerList = player.server.getPlayerManager().getPlayerList();

		/* check if the destination player is in the requests */
		if (TP.containsKey(player.getUuid())) {
			/* search through all players on the server until the origin player is found */
			for (ServerPlayerEntity search : playerList) {
				if (search.getUuid().equals(TP.get(player.getUuid()))) {
					originPlayer = search;
				}
			}

			/* throw an exception if the origin player is no longer in the server */
			if (originPlayer == null) {
				TP.remove(player.getUuid());
				throw new CommandException(new StringTextComponent("Requesting player no longer online!"));
			}

			/* send message confirming that the TPA request was accepted to both players */
			final TextComponent requestAcceptedFromMessage = new StringTextComponent(String.format("\u00a76TPA request from \u00a74%s\u00a76 accepted. \u00a76Teleporting...", originPlayer.getDisplayName().getText()));
			final TextComponent requestAcceptedToMessage = new StringTextComponent(String.format("\u00a76TPA request to \u00a74%s\u00a76 accepted. \u00a76Teleporting...", player.getDisplayName().getText()));

			originPlayer.addChatMessage(requestAcceptedToMessage, false);
			commandContext.getSource().sendFeedback(requestAcceptedFromMessage, false);

			/* teleport origin player to destination player */
			originPlayer.networkHandler.teleportRequest(player.x, player.y, player.z, player.yaw, player.pitch);

			TP.remove(player.getUuid());

			return 1;
		} else {
			throw new CommandException(new StringTextComponent("You have no requests to accept!"));
		}
	}

	private static int onTpDenyCommand(CommandContext<ServerCommandSource> commandContext, ServerPlayerEntity player) throws CommandSyntaxException {
		ServerPlayerEntity originPlayer = null;
		final List<ServerPlayerEntity> playerList = player.server.getPlayerManager().getPlayerList();

		/* check if the destination player is in the requests */
		if (TP.containsKey(player.getUuid())) {
			/* search for origin player in player list */
			for (ServerPlayerEntity search : playerList) {
				if (search.getUuid().equals(TP.get(player.getUuid()))) {
					originPlayer = search;
				}
			}

			/* throw an exception if the origin player is no longer in the server */
			if (originPlayer == null) {
				TP.remove(player.getUuid());
				throw new CommandException(new StringTextComponent("Requesting player no longer online!"));
			}

			/* send message confirming that the request was denied to both players */
			final TextComponent requestDeniedFromMessage = new StringTextComponent(String.format("\u00a7cTPA request from \u00a74%s\u00a7c denied.", originPlayer.getDisplayName().getText()));
			final TextComponent requestDeniedToMessage = new StringTextComponent(String.format("\u00a7cTPA request to \u00a74%s\u00a7c denied.", player.getDisplayName().getText()));

			commandContext.getSource().sendFeedback(requestDeniedFromMessage, false);
			originPlayer.addChatMessage(requestDeniedToMessage, false);

			TP.remove(player.getUuid());

			return 1;
		} else {
			throw new CommandException(new StringTextComponent("You have no requests to deny!"));
		}
	}

	public static HashBiMap<UUID, UUID> getRequests() {
		return TP;
	}
}
