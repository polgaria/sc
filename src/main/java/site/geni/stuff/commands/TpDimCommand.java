package site.geni.stuff.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandException;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;

public class TpDimCommand {
	public static void onCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(ServerCommandManager.literal("tpdim").requires(
				source -> source.hasPermissionLevel(2)
		).then(
				ServerCommandManager.argument("dimension", IntegerArgumentType.integer()).executes(context -> {
					ServerPlayerEntity player = context.getSource().getPlayer();
					int dimID = IntegerArgumentType.getInteger(context, "dimension");
					DimensionType dimensionType = Registry.DIMENSION.getInt(dimID);

					if (dimensionType != null) {
						TextComponent tpMessage = new StringTextComponent("Teleporting to " + dimensionType.toString() + "...").getTextComponent().applyFormat(TextFormat.YELLOW);
						context.getSource().sendFeedback(tpMessage, false);

						player.setInPortal(player.getPos());
						player.changeDimension(dimensionType);

						return 1;
					} else
						throw new CommandException(new StringTextComponent("Dimension " + dimID + "does not exist."));
				})));
	}
}
