package site.geni.stuff;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import site.geni.stuff.commands.*;

@SuppressWarnings("unused")
public class StuffMod implements ModInitializer {
	/* registers commands */
	private static void regCommands() {
		DayNightCommand.register();
		HealCommand.register();
		ItemCommand.register();
		ListCommand.register();
		SeedCommand.register();
		TimeCommand.register();
		TpaCommand.register();
		TpDimCommand.register();
		WeatherCommand.register();

		LogManager.getLogger().info("[stuff] Commands registered.");
	}

	@Override
	public void onInitialize() {
		regCommands();
	}
}
