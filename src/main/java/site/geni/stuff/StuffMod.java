package site.geni.stuff;

import net.fabricmc.api.ModInitializer;
import site.geni.stuff.commands.*;

@SuppressWarnings("unused")
public class StuffMod implements ModInitializer {
	@Override
	public void onInitialize() {
		regCommands();
	}

	/* registers commands */
	private static void regCommands() {
		DayNightCommand.register();
		ListCommand.register();
		SeedCommand.register();
		TpaCommand.register();
		TpDimCommand.register();
		WeatherCommand.register();
		TimeCommand.register();
	}
}
