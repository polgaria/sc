package site.geni.stuff.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.minecraft.command.CommandException;
import net.minecraft.command.arguments.DimensionArgumentType;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.world.dimension.DimensionType;
import site.geni.stuff.util.AutoAppendTextComponent;
import site.geni.stuff.util.AutoFormatTextComponent;

public class TpDimCommand {
	public static void register() {
		/* register tpdim command */
		ServerStartCallback.EVENT.register(
				server -> server.getCommandManager().getDispatcher().register(
						ServerCommandManager.literal("tpdim").requires(
								source -> source.hasPermissionLevel(4)
						).then(
								ServerCommandManager.argument(
										"dimension", DimensionArgumentType.create()
								).executes(
										context -> onCommand(context, DimensionArgumentType.getDimensionArgument(context, "dimension"))
								)
						)
				)
		);
	}

	private static int onCommand(CommandContext<ServerCommandSource> context, DimensionType dimensionType) throws CommandSyntaxException {
		final ServerPlayerEntity player = context.getSource().getPlayer();

		if (dimensionType != null && dimensionType != player.dimension) {
			final TextComponent dimension = new AutoFormatTextComponent(dimensionType.toString(), TextFormat.DARK_RED);

			final TextComponent tpMessage = new AutoAppendTextComponent(TextFormat.GOLD, "Teleporting to ", dimension, "...");
			context.getSource().sendFeedback(tpMessage, false);

			player.setInPortal(player.getPos());
			player.changeDimension(dimensionType);

			return 1;
		} else if (dimensionType == player.dimension && dimensionType != null) {
			throw new CommandException(new StringTextComponent(String.format("You are already in %s!", dimensionType.toString())));
		} else
			throw new CommandException(new StringTextComponent("Unexpected error."));
	}
}
