package site.geni.stuff.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.events.ServerEvent;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sortme.ChatMessageType;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextFormatter;

import java.util.Collection;
import java.util.Collections;

public class HealCommand {
	public static void register() {
		/* registers heal command */
		ServerEvent.START.register(
				server -> server.getCommandManager().getDispatcher().register(
						ServerCommandManager.literal("heal").requires(serverCommandSource ->
								serverCommandSource.hasPermissionLevel(2)
						).executes(
								commandContext -> onCommand(commandContext.getSource(), Collections.singletonList(commandContext.getSource().getPlayer()))
						)
								.then(
										ServerCommandManager.argument("targets", EntityArgumentType.multiplePlayer()).executes(
												commandContext -> onCommand(commandContext.getSource(), EntityArgumentType.method_9312(commandContext, "targets"))
										)
								)
				)
		);
	}

	private static int onCommand(ServerCommandSource commandSource, Collection<ServerPlayerEntity> players) throws CommandSyntaxException {
		for (ServerPlayerEntity player : players) {
			player.setHealth(player.getHealthMaximum());
			player.getHungerManager().setFoodLevel(20);

			if (!player.equals(commandSource.getPlayer())) {
				player.sendChatMessage(new StringTextComponent("\u00a76You have been healed!"), ChatMessageType.CHAT);
			}
		}

		commandSource.sendFeedback(new StringTextComponent(String.format("\u00a76Healed \u00a74%s\u00a76!", TextFormatter.join(players, PlayerEntity::getDisplayName).getString())), false);

		return 1;
	}
}
