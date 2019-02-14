package site.geni.stuff.commands;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import site.geni.stuff.util.AutoAppendTextComponent;
import site.geni.stuff.util.AutoFormatTextComponent;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class TimeCommand {
	private static final int TICKS_AT_MIDNIGHT = 18000;
	private static final int TICKS_PER_DAY = 24000;
	private static final int TICKS_PER_HOUR = 1000;
	private static final double TICKS_PER_MINUTE = 1000d / 60d;
	private static final double TICKS_PER_SECOND = 1000d / 60d / 60d;


	public static void register() {
		/* register time command */
		ServerStartCallback.EVENT.register(
				server -> server.getCommandManager().getDispatcher().register(
						ServerCommandManager.literal("time").executes(
								context -> onCommand(context)
						)
				)
		);
	}

	private static int onCommand(CommandContext<ServerCommandSource> context) {
		final ServerCommandSource commandSource = context.getSource();

		final long timeOfDay = commandSource.getWorld().getTimeOfDay();
		final TextComponent date = new AutoFormatTextComponent(getTime(timeOfDay).getTime().toString(), TextFormat.GREEN);
		final TextComponent timeOfDayText = new AutoFormatTextComponent(Long.toString(timeOfDay), TextFormat.GREEN);

		commandSource.sendFeedback(new AutoAppendTextComponent(TextFormat.GOLD, "The date and time is ", date, " (", timeOfDayText, ")"), false);
		return (int) (timeOfDay);
	}

	public static Calendar getTime(long ticks) {
		// very cool EssentialsX calculations
		ticks = ticks - TICKS_AT_MIDNIGHT + TICKS_PER_DAY;

		final long days = ticks / TICKS_PER_DAY;
		ticks -= days * TICKS_PER_DAY;

		final long hours = ticks / TICKS_PER_HOUR;
		ticks -= hours * TICKS_PER_HOUR;

		final long minutes = (long) Math.floor(ticks / TICKS_PER_MINUTE);
		final double dticks = ticks - minutes * TICKS_PER_MINUTE;

		final long seconds = (long) Math.floor(dticks / TICKS_PER_SECOND);

		final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.ENGLISH);
		cal.setLenient(true);

		cal.set(0, Calendar.JANUARY, 1, 0, 0, 0);
		cal.add(Calendar.DAY_OF_YEAR, (int) days);
		cal.add(Calendar.HOUR_OF_DAY, (int) hours);
		cal.add(Calendar.MINUTE, (int) minutes);
		cal.add(Calendar.SECOND, (int) seconds + 1);

		return cal;
	}
}
