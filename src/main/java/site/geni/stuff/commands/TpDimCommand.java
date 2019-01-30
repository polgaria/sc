package site.geni.stuff.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.events.ServerEvent;
import net.minecraft.command.CommandException;
import net.minecraft.command.arguments.DimensionArgumentType;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.world.dimension.DimensionType;

public class TpDimCommand {
	public static void register() {
		/* register tpdim command */
		ServerEvent.START.register(
				server -> server.getCommandManager().getDispatcher().register(
						ServerCommandManager.literal("tpdim").requires(
								source -> source.hasPermissionLevel(1)
						).then(
								ServerCommandManager.argument(
										"dimension", DimensionArgumentType.create()
								).executes(
										context -> onCommand(context)
								)
						)
				)
		);
	}

	private static int onCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		final ServerPlayerEntity player = context.getSource().getPlayer();
		final DimensionType dimensionType = DimensionArgumentType.getDimensionArgument(context, "dimension");

		if (dimensionType != null && dimensionType != player.dimension) {
			final TextComponent tpMessage = new StringTextComponent(String.format("\u00a76Teleporting to %s...", dimensionType.toString()));
			context.getSource().sendFeedback(tpMessage, false);

			player.setInPortal(player.getPos());
			player.changeDimension(dimensionType);

			return 1;
		} else if (dimensionType == player.dimension) {
			final TextComponent alreadyInDimMessage = new StringTextComponent(String.format("You are already in %s!", dimensionType.toString()));
			throw new CommandException(alreadyInDimMessage);
		} else
			throw new CommandException(new StringTextComponent("Unknown error."));
	}
}
