package site.geni.stuff.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;

public class DayNightCommand {
	private final static TextComponent dayMessage = new StringTextComponent("Time set to day. (0)").getTextComponent().applyFormat(TextFormat.YELLOW);
	private final static TextComponent nightMessage = new StringTextComponent("Time set to night. (13000)").getTextComponent().applyFormat(TextFormat.YELLOW);

	public static void onCycleCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
				ServerCommandManager.literal("cycle").requires(
				source -> source.hasPermissionLevel(1)
		).executes(context -> {
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
		}));
	}

	public static void onDayCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
				ServerCommandManager.literal("day").requires(
				source -> source.hasPermissionLevel(1)
		).executes(context -> {
			ServerPlayerEntity player = context.getSource().getPlayer();

			player.world.setTimeOfDay(0);
			context.getSource().sendFeedback(dayMessage, false);

			return 1;
		}));
	}

	public static void onNightCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
				ServerCommandManager.literal("night").requires(
				source -> source.hasPermissionLevel(1)
		).executes(context -> {
			ServerPlayerEntity player = context.getSource().getPlayer();

			player.world.setTimeOfDay(13000);
			context.getSource().sendFeedback(nightMessage, false);

			return 1;
		}));
	}
}
