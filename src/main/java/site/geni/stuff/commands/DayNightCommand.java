package site.geni.stuff.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.events.ServerEvent;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;

public class DayNightCommand {
	private final static TextComponent dayMessage = new StringTextComponent("\u00a76Time set to day. (0)");
	private final static TextComponent nightMessage = new StringTextComponent("\u00a76Time set to night. (13000)");

	public static void register() {
		/* register cycle command */
		ServerEvent.START.register(
				server -> server.getCommandManager().getDispatcher().register(
						ServerCommandManager.literal("cycle").executes(
								context -> onCycleCommand(context)
						)
				)
		);

		/* register day command */
		ServerEvent.START.register(
				server -> server.getCommandManager().getDispatcher().register(
						ServerCommandManager.literal("day").executes(
								context -> onDayCommand(context)
						)
				)
		);

		/* register night command */
		ServerEvent.START.register(
				server -> server.getCommandManager().getDispatcher().register(
						ServerCommandManager.literal("night").executes(
								context -> onNightCommand(context)
						)
				)
		);
	}

	public static int onCycleCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity player = context.getSource().getPlayer();
		long time = player.world.getTimeOfDay();

		if (time >= 12516) {
			player.world.setTimeOfDay(0);
			context.getSource().sendFeedback(dayMessage, false);
		} else {
			player.world.setTimeOfDay(13000);
			context.getSource().sendFeedback(nightMessage, false);
		}
		return 1;
	}

	public static int onDayCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity player = context.getSource().getPlayer();

		player.world.setTimeOfDay(0);
		context.getSource().sendFeedback(dayMessage, false);

		return 1;
	}

	public static int onNightCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity player = context.getSource().getPlayer();

		player.world.setTimeOfDay(13000);
		context.getSource().sendFeedback(nightMessage, false);

		return 1;
	}
}
