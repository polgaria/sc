package site.geni.stuff;

import net.fabricmc.api.ModInitializer;
import site.geni.stuff.commands.TpDimCommand;
import site.geni.stuff.commands.DayNightCommand;
import site.geni.stuff.commands.TpaCommand;
import site.geni.stuff.commands.WeatherCommand;

@SuppressWarnings("unused")
public class StuffMod implements ModInitializer {
	@Override
	public void onInitialize() {
		regCommands();
	}

	/* registers commands */
	private static void regCommands() {
		TpDimCommand.register();
		DayNightCommand.register();
		WeatherCommand.register();
		TpaCommand.register();
	}
}
