package site.geni.stuff.commands;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.events.ServerEvent;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.StringTextComponent;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class TimeCommand {
	private static final int ticksAtMidnight = 18000;
	private static final int ticksPerDay = 24000;
	private static final int ticksPerHour = 1000;
	private static final double ticksPerMinute = 1000d / 60d;
	private static final double ticksPerSecond = 1000d / 60d / 60d;

	public static final String timeDateString = "\u00a76The date and time is \u00a7a%s\u00a76. (\u00a7a%d\u00a76)";

	public static void register() {
		/* register time command */
		ServerEvent.START.register(
				server -> server.getCommandManager().getDispatcher().register(
						ServerCommandManager.literal("time").executes(
								context -> onCommand(context)
						)
				)
		);
	}

	private static int onCommand(CommandContext<ServerCommandSource> context) {
		// very cool EssentialsX calculations
		final long timeOfDay = context.getSource().getWorld().getTimeOfDay();
		Calendar cal = getTime(timeOfDay);

		context.getSource().sendFeedback(new StringTextComponent(String.format(timeDateString, cal.getTime().toString(), timeOfDay)), false);
		return (int) (timeOfDay);
	}

	public static Calendar getTime(long ticks) {
		// very cool EssentialsX calculations
		ticks = ticks - ticksAtMidnight + ticksPerDay;

		final long days = ticks / ticksPerDay;
		ticks -= days * ticksPerDay;

		final long hours = ticks / ticksPerHour;
		ticks -= hours * ticksPerHour;

		final long minutes = (long) Math.floor(ticks / ticksPerMinute);
		final double dticks = ticks - minutes * ticksPerMinute;

		final long seconds = (long) Math.floor(dticks / ticksPerSecond);

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
