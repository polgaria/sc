package site.geni.stuff.commands;

import net.fabricmc.fabric.events.ServerEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormatter;

import java.util.List;
import java.util.function.Function;

public class ListCommand {
	public static void register() {
		/* registers list command */
		ServerEvent.START.register(
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
		PlayerManager playerManager = serverCommandSource.getMinecraftServer().getPlayerManager();
		List<ServerPlayerEntity> playerList = playerManager.getPlayerList();
		TextComponent textComponent = TextFormatter.join(playerList, function);

		serverCommandSource.sendFeedback(new StringTextComponent(String.format("\u00a76There are \u00a74%s\u00a76 out of \u00a74%s\u00a76 maximum players online:\u00a7r %s", playerList.size(), playerManager.getMaxPlayerCount(), textComponent.getFormattedText())), false);
		return playerList.size();
	}
}
