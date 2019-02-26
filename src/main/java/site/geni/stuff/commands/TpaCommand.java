package site.geni.stuff.commands;

import com.google.common.collect.HashBiMap;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.minecraft.command.CommandException;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import site.geni.stuff.util.AutoAppendTextComponent;
import site.geni.stuff.util.AutoFormatTextComponent;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TpaCommand {
	/* preparations for accept message */
	private final static TextComponent tpAccept = new AutoFormatTextComponent("/tpaccept", TextFormat.GREEN);
	private final static TextComponent tpDeny = new AutoFormatTextComponent("/tpadeny", TextFormat.GREEN);

	/* requests hashmap */
	private static HashBiMap<UUID, UUID> requests = HashBiMap.create();

	public static void register() {
		/* register tpa command */
		ServerStartCallback.EVENT.register(
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
		ServerStartCallback.EVENT.register(
				server -> server.getCommandManager().getDispatcher().register(
						ServerCommandManager.literal("tpaccept").executes(
								commandContext -> onTpAcceptCommand(commandContext, commandContext.getSource().getPlayer())
						)
				)
		);

		/* register tpadeny command */
		ServerStartCallback.EVENT.register(
				server -> server.getCommandManager().getDispatcher().register(
						ServerCommandManager.literal("tpdeny").executes(
								commandContext -> onTpDenyCommand(commandContext, commandContext.getSource().getPlayer())
						)
				)
		);
	}

	private static int onTpaCommand(CommandContext<ServerCommandSource> commandContext, ServerPlayerEntity originPlayer, ServerPlayerEntity destPlayer) {
		if (requests.containsKey(destPlayer.getUuid())) {
			throw new CommandException(new StringTextComponent("This player already has a pending TPA request!"));
		} else if (requests.containsValue(originPlayer.getUuid())) {
			throw new CommandException(new StringTextComponent("You've already sent a TPA request!"));
		}

		/* add players to requests */
		if (!originPlayer.equals(destPlayer)) {
			requests.put(destPlayer.getUuid(), originPlayer.getUuid());
		} else {
			throw new CommandException(new StringTextComponent("You can't teleport to yourself!"));
		}


		/* prepare for messages */
		final TextComponent originPlayerName = new AutoFormatTextComponent(originPlayer.getDisplayName().getString(), TextFormat.DARK_RED);
		final TextComponent destPlayerName = new AutoFormatTextComponent(destPlayer.getDisplayName().getString(), TextFormat.DARK_RED);

		/* send message confirming to origin player that the request has been sent */
		final TextComponent sentTpaRequest = new AutoAppendTextComponent(TextFormat.GOLD, "TPA request to ", destPlayerName, " has been sent.");

		commandContext.getSource().sendFeedback(sentTpaRequest, false);


		/* send message to destination player alerting them of the pending TPA request */
		final TextComponent receivedTpaRequest = new AutoAppendTextComponent(TextFormat.GOLD, originPlayerName, " has sent you a TPA request! Accept with ", tpAccept, " or deny with ", tpDeny);
		final TextComponent tpaRequestExpiresIn = new AutoFormatTextComponent("You have 30 seconds to respond until the TPA request expires.", TextFormat.GOLD);

		destPlayer.addChatMessage(receivedTpaRequest, false);
		destPlayer.addChatMessage(tpaRequestExpiresIn, false);


		/* runnable for when the request should expire */
		final Runnable expire = () -> {
			/* if request hasn't already been removed */
			if (requests.containsKey(destPlayer.getUuid())) {
				/* remove expired TPA request from requests */
				requests.remove(destPlayer.getUuid());

				/* send messages to both players alerting them that the TPA request has expired */
				final TextComponent requestExpiredFromMessage = new AutoAppendTextComponent(TextFormat.GOLD, "TPA request from ", originPlayerName, "has expired.");
				final TextComponent requestExpiredToMessage = new AutoAppendTextComponent(TextFormat.GOLD, "TPA request to ", destPlayerName, "has expired.");

				commandContext.getSource().sendFeedback(requestExpiredToMessage, false);
				destPlayer.addChatMessage(requestExpiredFromMessage, false);
			}
		};

		/* schedule an executor to expire the request after 30 seconds */
		Executors.newScheduledThreadPool(1).schedule(expire, 30, TimeUnit.SECONDS);

		return 1;
	}

	private static int onTpAcceptCommand(CommandContext<ServerCommandSource> commandContext, ServerPlayerEntity player) {
		/* check if the destination player is in the requests */
		if (requests.containsKey(player.getUuid())) {
			/* try to get destination player */
			final ServerPlayerEntity originPlayer = player.server.getPlayerManager().getPlayer(requests.get(player.getUuid()));

			/* throw an exception if the origin player is no longer in the server */
			if (originPlayer == null) {
				requests.remove(player.getUuid());
				throw new CommandException(new StringTextComponent("Requesting player no longer online!"));
			}


			/* send message confirming that the TPA request was accepted to both players */
			final TextComponent originPlayerName = new AutoFormatTextComponent(originPlayer.getDisplayName().getString(), TextFormat.DARK_RED);
			final TextComponent destPlayerName = new AutoFormatTextComponent(player.getDisplayName().getString(), TextFormat.DARK_RED);

			final TextComponent requestAcceptedFromMessage = new AutoAppendTextComponent(TextFormat.GOLD, "TPA request from ", originPlayerName, "has been accepted. Teleporting...");
			final TextComponent requestAcceptedToMessage = new AutoAppendTextComponent(TextFormat.GOLD, "TPA request to ", destPlayerName, "has been accepted. Teleporting...");

			originPlayer.addChatMessage(requestAcceptedToMessage, false);
			commandContext.getSource().sendFeedback(requestAcceptedFromMessage, false);


			/* teleport origin player to destination player */
			originPlayer.networkHandler.teleportRequest(player.x, player.y, player.z, player.yaw, player.pitch);

			requests.remove(player.getUuid());

			return 1;
		} else {
			throw new CommandException(new StringTextComponent("You have no requests to accept!"));
		}
	}

	private static int onTpDenyCommand(CommandContext<ServerCommandSource> commandContext, ServerPlayerEntity player) {
		ServerPlayerEntity originPlayer = null;
		final List<ServerPlayerEntity> playerList = player.server.getPlayerManager().getPlayerList();

		/* check if the destination player is in the requests */
		if (requests.containsKey(player.getUuid())) {
			/* search for origin player in player list */
			for (ServerPlayerEntity search : playerList) {
				if (search.getUuid().equals(requests.get(player.getUuid()))) {
					originPlayer = search;
				}
			}

			/* throw an exception if the origin player is no longer in the server */
			if (originPlayer == null) {
				requests.remove(player.getUuid());
				throw new CommandException(new StringTextComponent("Requesting player no longer online!"));
			}


			/* send message confirming that the request was denied to both players */
			final TextComponent originPlayerName = new AutoFormatTextComponent(originPlayer.getDisplayName().getString(), TextFormat.DARK_RED);
			final TextComponent destPlayerName = new AutoFormatTextComponent(player.getDisplayName().getString(), TextFormat.DARK_RED);

			final TextComponent requestDeniedFromMessage = new AutoAppendTextComponent(TextFormat.RED, "TPA request from ", originPlayerName, "denied.");
			final TextComponent requestDeniedToMessage = new AutoAppendTextComponent(TextFormat.RED, "TPA request to ", destPlayerName, "denied.");

			commandContext.getSource().sendFeedback(requestDeniedFromMessage, false);
			originPlayer.addChatMessage(requestDeniedToMessage, false);

			requests.remove(player.getUuid());


			return 1;
		} else {
			throw new CommandException(new StringTextComponent("You have no requests to deny!"));
		}
	}

	public static HashBiMap<UUID, UUID> getRequests() {
		return requests;
	}
}
