package site.geni.stuff.commands;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.events.ServerEvent;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.*;
import net.minecraft.text.event.ClickEvent;

public class SeedCommand {
	public static void register() {
		/* register seed command */
		ServerEvent.START.register(
				server -> server.getCommandManager().getDispatcher().register(
						ServerCommandManager.literal("seed").executes(
								commandContext -> onCommand(commandContext)
						)
				)
		);
	}

	private static int onCommand(CommandContext<ServerCommandSource> commandContext) {
		final long seed = commandContext.getSource().getWorld().getSeed();

		final TextComponent textComponent_1 = TextFormatter.bracketed((new StringTextComponent(String.valueOf(seed))).modifyStyle(style ->
			style.setColor(TextFormat.GREEN).setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.valueOf(seed))).setInsertion(String.valueOf(seed))
		));
		commandContext.getSource().sendFeedback(new TranslatableTextComponent("commands.seed.success", textComponent_1).applyFormat(TextFormat.GOLD), false);

		return (int) seed;
	}
}
