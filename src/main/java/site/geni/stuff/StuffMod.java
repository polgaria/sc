package site.geni.stuff;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
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

	/* registers commands */
	private static void regCommands() {
		final boolean dedicated;

		/* check if running in client or dedicated server */
		EnvType env = FabricLoader.getInstance().getEnvironmentType();
		if (env == EnvType.CLIENT) {
			dedicated = false;
		} else {
			dedicated = true;
		}

		CommandRegistry.INSTANCE.register(dedicated, DayNightCommand::onCycleCommand);
		CommandRegistry.INSTANCE.register(dedicated, DayNightCommand::onDayCommand);
		CommandRegistry.INSTANCE.register(dedicated, DayNightCommand::onNightCommand);
		CommandRegistry.INSTANCE.register(dedicated, TpDimCommand::onCommand);
		CommandRegistry.INSTANCE.register(dedicated, WeatherCommand::onRainCommand);
		CommandRegistry.INSTANCE.register(dedicated, WeatherCommand::onThunderCommand);

	}
}
