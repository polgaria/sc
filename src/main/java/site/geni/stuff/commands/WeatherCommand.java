package site.geni.stuff.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.events.ServerEvent;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.world.level.LevelProperties;

public class WeatherCommand {
	private final static TextComponent startRainMessage = new StringTextComponent("Rain started.").getTextComponent().applyFormat(TextFormat.YELLOW);
	private final static TextComponent stopRainMessage = new StringTextComponent("Rain stopped.").getTextComponent().applyFormat(TextFormat.YELLOW);

	private final static TextComponent startThunderMessage = new StringTextComponent("Thunder started.").getTextComponent().applyFormat(TextFormat.YELLOW);
	private final static TextComponent stopThunderMessage = new StringTextComponent("Thunder stopped.").getTextComponent().applyFormat(TextFormat.YELLOW);

	public static void register() {
		/* rain command */
		ServerEvent.START.register(
				server -> server.getCommandManager().getDispatcher().register(
						ServerCommandManager.literal("rain").executes(
								context -> onRainCommand(context)
						)
				)
		);

		/* thunder command */
		ServerEvent.START.register(
				server -> server.getCommandManager().getDispatcher().register(
						ServerCommandManager.literal("thunder").executes(
								context -> onThunderCommand(context)
						)
				)
		);
	}

	public static int onRainCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity player = context.getSource().getPlayer();
		if (setRaining(player.getServerWorld())) {
			player.server.getPlayerManager().sendToAll(startRainMessage);
		} else {
			player.server.getPlayerManager().sendToAll(stopRainMessage);
		}
		return 1;
	}

	public static int onThunderCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity player = context.getSource().getPlayer();
		if (setThundering(player.getServerWorld())) {
			player.server.getPlayerManager().sendToAll(startThunderMessage);
		} else {
			player.server.getPlayerManager().sendToAll(stopThunderMessage);
		}
		return 1;
	}

	private static boolean setRaining(ServerWorld world) {
		LevelProperties properties = world.getLevelProperties();
		if (properties.isRaining()) {
			properties.setRainTime(0);
			properties.setRaining(false);
			return false;
		} else {
			properties.setRainTime(world.random.nextInt(12000) + 12000);
			properties.setRaining(true);
			return true;
		}
	}

	private static boolean setThundering(ServerWorld world) {
		LevelProperties properties = world.getLevelProperties();
		if (properties.isThundering()) {
			properties.setThunderTime(0);
			properties.setThundering(false);
			return false;
		} else {
			properties.setThunderTime(world.random.nextInt(12000) + 3600);
			properties.setThundering(true);
			return true;
		}
	}
}
