package site.geni.stuff.commands;


import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TextFormatter;
import site.geni.stuff.util.AutoAppendTextComponent;
import site.geni.stuff.util.AutoFormatTextComponent;

import java.util.List;
import java.util.function.Function;

public class ListCommand {
	public static void register() {
		/* registers list command */
		ServerStartCallback.EVENT.register(
				server -> server.getCommandManager().getDispatcher().register(
						ServerCommandManager.literal("list").executes(
								context -> get_usernames(context.getSource())
						).then(
								ServerCommandManager.literal(
										"uuids"
								).executes(
										context -> get_uuids(context.getSource())
								)
						)
				)
		);
	}

	private static int get_usernames(ServerCommandSource serverCommandSource_1) {
		return send_list(serverCommandSource_1, PlayerEntity::getDisplayName);
	}

	private static int get_uuids(ServerCommandSource serverCommandSource_1) {
		return send_list(serverCommandSource_1, PlayerEntity::method_7306);
	}

	private static int send_list(ServerCommandSource serverCommandSource, Function<ServerPlayerEntity, TextComponent> function) {
		final PlayerManager playerManager = serverCommandSource.getMinecraftServer().getPlayerManager();
		final List<ServerPlayerEntity> playerList = playerManager.getPlayerList();

		final TextComponent textComponent = new AutoFormatTextComponent(TextFormatter.join(playerList, function), TextFormat.RESET);

		serverCommandSource.sendFeedback(new AutoAppendTextComponent(TextFormat.GOLD, "There are ", playerList.size(), " out of ", playerManager.getMaxPlayerCount(), " maximum players online: ", textComponent), false);

		return playerList.size();
	}
}
