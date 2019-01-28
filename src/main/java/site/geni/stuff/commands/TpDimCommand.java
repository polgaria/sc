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
		/* tpdim command */
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

	public static int onCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity player = context.getSource().getPlayer();
		DimensionType dimensionType = DimensionArgumentType.getDimensionArgument(context, "dimension");

		if (dimensionType != null && dimensionType != player.dimension) {
			TextComponent tpMessage = new StringTextComponent("Teleporting to " + dimensionType.toString() + "...").getTextComponent().applyFormat(TextFormat.YELLOW);
			context.getSource().sendFeedback(tpMessage, false);

			player.setInPortal(player.getPos());
			player.changeDimension(dimensionType);

			return 1;
		} else if (dimensionType == player.dimension) {
			TextComponent alreadyInDimMessage = new StringTextComponent("You are already in " + dimensionType.toString() + "!").getTextComponent();
			throw new CommandException(alreadyInDimMessage);
		} else
			throw new CommandException(new StringTextComponent("Unknown error."));
	}
}
