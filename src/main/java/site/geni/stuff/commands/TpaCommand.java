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
import net.minecraft.text.TranslatableTextComponent;

import java.util.List;
import java.util.UUID;

public class TpaCommand {
	public static HashBiMap<UUID, UUID> tp = HashBiMap.create();

	public static void register() {
		/* register tpa command */
		ServerEvent.START.register(
				server -> server.getCommandManager().getDispatcher().register(
						ServerCommandManager.literal("tpa").then(
								ServerCommandManager.argument(
										"player", EntityArgumentType.onePlayer()
								).executes(
										context -> onTpaCommand(context)
								)
						)
				)
		);

		/* register tpaccept command */
		ServerEvent.START.register(
				server -> server.getCommandManager().getDispatcher().register(
						ServerCommandManager.literal("tpaccept").executes(
								context -> onTpAcceptCommand(context)
						)
				)
		);

		/* register tpadeny command */
		ServerEvent.START.register(
				server -> server.getCommandManager().getDispatcher().register(
						ServerCommandManager.literal("tpdeny").executes(
								context -> onTpDenyCommand(context)
						)
				)
		);
	}

	public static int onTpaCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity originPlayer = context.getSource().getPlayer();
		ServerPlayerEntity destPlayer = EntityArgumentType.method_9315(context, "player");

		if (tp.containsKey(destPlayer.getUuid())) {
			throw new CommandException(new StringTextComponent("This player already has a pending TPA request!"));
		} else if (tp.containsValue(originPlayer.getUuid())) {
			throw new CommandException(new StringTextComponent("You've already sent a TPA request!"));
		}

		/* add players to requests */
		if (!originPlayer.equals(destPlayer)) {
			tp.put(destPlayer.getUuid(), originPlayer.getUuid());
		} else {
			throw new CommandException(new StringTextComponent("You can't teleport to yourself!"));
		}

		/* send message confirming to origin player that the request has been sent */
		context.getSource().sendFeedback(new TranslatableTextComponent(String.format("\u00a76TPA request to \u00a74%s\u00a76 sent.", destPlayer.getDisplayName().getText())), false);

		/* send message to destination player alerting them of the TPA request */
		destPlayer.addChatMessage(new TranslatableTextComponent(String.format("\u00a74%s \u00a76has sent you a TPA request! \u00a76Accept with \u00a72/tpaccept\u00a76 or deny with \u00a72/tpdeny\u00a76.", originPlayer.getDisplayName().getText())), false);
		destPlayer.addChatMessage(new StringTextComponent("\u00a76You have 30 seconds to respond until the TPA request \u00a76expires."), false);

		/* schedule a timer for 30 seconds */
		new java.util.Timer().schedule(
				new java.util.TimerTask() {
					@Override
					public void run() {
						if (tp.containsKey(destPlayer.getUuid())) {
							/* remove expired TPA request from requests */
							tp.remove(destPlayer.getUuid());

							/* send messages to both players alerting them that the TPA request has expired */
							TextComponent requestExpiredFromMessage = new StringTextComponent(String.format("\u00a7cTPA request from \u00a74%s\u00a7c expired.", originPlayer.getDisplayName().getText()));
							TextComponent requestExpiredToMessage = new StringTextComponent(String.format("\u00a7cTPA request to \u00a74%s\u00a7c expired.", destPlayer.getDisplayName().getText()));

							context.getSource().sendFeedback(requestExpiredToMessage, false);
							destPlayer.addChatMessage(requestExpiredFromMessage, false);
						}
					}
				},
				30000
		);

		return 1;
	}

	public static int onTpAcceptCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity player = context.getSource().getPlayer();
		ServerPlayerEntity originPlayer = null;
		List<ServerPlayerEntity> playerList = player.server.getPlayerManager().getPlayerList();

		/* check if the destination player is in the requests */
		if (tp.containsKey(player.getUuid())) {
			/* search through all players on the server until the origin player is found */
			for (ServerPlayerEntity search : playerList) {
				if (search.getUuid().equals(tp.get(player.getUuid()))) {
					originPlayer = search;
				}
			}

			/* throw an exception if the origin player is no longer in the server */
			if (originPlayer == null) {
				tp.remove(player.getUuid());
				throw new CommandException(new StringTextComponent("Requesting player no longer online!"));
			}

			/* send message confirming that the TPA request was accepted to both players */
			TextComponent requestAcceptedFromMessage = new StringTextComponent(String.format("\u00a76TPA request from \u00a74%s\u00a76 accepted. \u00a76Teleporting...", originPlayer.getDisplayName().getText()));
			TextComponent requestAcceptedToMessage = new StringTextComponent(String.format("\u00a76TPA request to \u00a74%s\u00a76 accepted. \u00a76Teleporting...", player.getDisplayName().getText()));

			originPlayer.addChatMessage(requestAcceptedToMessage, false);
			context.getSource().sendFeedback(requestAcceptedFromMessage, false);

			/* teleport origin player to destination player */
			originPlayer.networkHandler.teleportRequest(player.x, player.y, player.z, player.yaw, player.pitch);
			tp.remove(player.getUuid());
			return 1;
		} else {
			throw new CommandException(new StringTextComponent(String.format("You have no requests to accept!")));
		}
	}

	public static int onTpDenyCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity player = context.getSource().getPlayer();
		ServerPlayerEntity originPlayer = null;
		List<ServerPlayerEntity> playerList = player.server.getPlayerManager().getPlayerList();

		/* check if the destination player is in the requests */
		if (tp.containsKey(player.getUuid())) {
			/* search for origin player in player list */
			for (ServerPlayerEntity search : playerList) {
				if (search.getUuid().equals(tp.get(player.getUuid()))) {
					originPlayer = search;
				}
			}

			/* throw an exception if the origin player is no longer in the server */
			if (originPlayer == null) {
				tp.remove(player.getUuid());
				throw new CommandException(new StringTextComponent(String.format("Requesting player no longer online!")));
			}

			/* send message confirming that the request was denied to both players */
			TextComponent requestDeniedFromMessage = new StringTextComponent(String.format("\u00a7cTPA request from \u00a74%s\u00a7c denied.", originPlayer.getDisplayName().getText()));
			TextComponent requestDeniedToMessage = new StringTextComponent(String.format("\u00a7cTPA request to \u00a74%s\u00a7c denied.", player.getDisplayName().getText()));

			context.getSource().sendFeedback(requestDeniedFromMessage, false);
			originPlayer.addChatMessage(requestDeniedToMessage, false);

			tp.remove(player.getUuid());

			return 1;
		} else {
			throw new CommandException(new StringTextComponent(String.format("You have no requests to deny!")));
		}
	}
}
