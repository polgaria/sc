package site.geni.stuff;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.commands.CommandRegistry;
import site.geni.stuff.commands.DayNightCommand;
import site.geni.stuff.commands.TpDimCommand;
import site.geni.stuff.commands.WeatherCommand;

public class StuffMod implements ModInitializer {
	@Override
	public void onInitialize() {
		regCommands();
	}

	private static void regCommands() {
		CommandRegistry.INSTANCE.register(true, TpDimCommand::onCommand);
		CommandRegistry.INSTANCE.register(true, DayNightCommand::onCycleCommand);
		CommandRegistry.INSTANCE.register(true, DayNightCommand::onDayCommand);
		CommandRegistry.INSTANCE.register(true, DayNightCommand::onNightCommand);
		CommandRegistry.INSTANCE.register(true, WeatherCommand::onRainCommand);
		CommandRegistry.INSTANCE.register(true, WeatherCommand::onThunderCommand);

	}
}
