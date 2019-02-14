package site.geni.stuff.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.world.level.LevelProperties;
import site.geni.stuff.util.AutoFormatTextComponent;

public class WeatherCommand {
	private final static TextComponent START_RAIN_MESSAGE = new AutoFormatTextComponent("Rain started.", TextFormat.GOLD);
	private final static TextComponent STOP_RAIN_MESSAGE = new AutoFormatTextComponent("Rain stopped.", TextFormat.GOLD);

	private final static TextComponent START_THUNDER_MESSAGE = new AutoFormatTextComponent("Thunder started.", TextFormat.GOLD);
	private final static TextComponent STOP_THUNDER_MESSAGE = new AutoFormatTextComponent("Thunder stopped.", TextFormat.GOLD);


	public static void register() {
		/* register rain command */
		ServerStartCallback.EVENT.register(
				server -> server.getCommandManager().getDispatcher().register(
						ServerCommandManager.literal("rain").executes(
								context -> onRainCommand(context)
						)
				)
		);

		/* register thunder command */
		ServerStartCallback.EVENT.register(
				server -> server.getCommandManager().getDispatcher().register(
						ServerCommandManager.literal("thunder").executes(
								context -> onThunderCommand(context)
						)
				)
		);
	}

	private static int onRainCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		final ServerPlayerEntity player = context.getSource().getPlayer();
		if (setRaining(player.getServerWorld())) {
			player.server.getPlayerManager().sendToAll(START_RAIN_MESSAGE);
		} else {
			player.server.getPlayerManager().sendToAll(STOP_RAIN_MESSAGE);
		}

		return 1;
	}

	private static int onThunderCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		final ServerPlayerEntity player = context.getSource().getPlayer();
		if (setThundering(player.getServerWorld())) {
			player.server.getPlayerManager().sendToAll(START_THUNDER_MESSAGE);
		} else {
			player.server.getPlayerManager().sendToAll(STOP_THUNDER_MESSAGE);
		}

		return 1;
	}

	private static boolean setRaining(ServerWorld world) {
		final LevelProperties properties = world.getLevelProperties();
		if (properties.isRaining()) {
			properties.setClearWeatherTime(6000);
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
		final LevelProperties properties = world.getLevelProperties();
		if (properties.isThundering()) {
			properties.setClearWeatherTime(6000);
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
