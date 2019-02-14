package site.geni.stuff.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.minecraft.class_2290;
import net.minecraft.command.CommandException;
import net.minecraft.command.arguments.ItemStackArgumentType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextFormat;

public class ItemCommand {
	public static void register() {
		/* registers i command */
		ServerStartCallback.EVENT.register(
				server -> server.getCommandManager().getDispatcher().register(
						ServerCommandManager.literal("i").requires(serverCommandSource ->
								serverCommandSource.hasPermissionLevel(4)
						).then(
								ServerCommandManager.argument("item", ItemStackArgumentType.create()).executes(
										commandContext -> onCommand(commandContext, ItemStackArgumentType.method_9777(commandContext, "item"), 1)
								).then(
										ServerCommandManager.argument("count", IntegerArgumentType.integer(1)).executes(
												commandContext -> onCommand(commandContext, ItemStackArgumentType.method_9777(commandContext, "item"), IntegerArgumentType.getInteger(commandContext, "count"))
										)
								)
						)
				)
		);
	}

	private static int onCommand(CommandContext<ServerCommandSource> context, class_2290 item, int amount) throws CommandSyntaxException {
		final ServerPlayerEntity player = context.getSource().getPlayer();

		ItemStack itemStack = item.method_9781(amount, false);
		boolean canInsertItem = context.getSource().getPlayer().inventory.insertStack(itemStack);

		/* if the player's inventory is full */
		if (!canInsertItem) {
			throw new CommandException(new StringTextComponent("Your inventory is full!"));
		} else if (itemStack.isEmpty()) {
			itemStack.setAmount(1);

			final ItemEntity itemEntity = player.dropItem(itemStack, false);
			if (itemEntity != null) {
				itemEntity.method_6987();
			}

			player.world.playSound(null, player.x, player.y, player.z, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYER, 0.2F, ((player.getRand().nextFloat() - player.getRand().nextFloat()) * 0.7F + 1.0F) * 2.0F);
			player.containerPlayer.sendContentUpdates();
		} else {
			final ItemEntity itemEntity = player.dropItem(itemStack, false);

			if (itemEntity != null) {
				itemEntity.resetPickupDelay();
				itemEntity.setOwner(player.getUuid());
			}
		}

		context.getSource().sendFeedback(new StringTextComponent("Gave you ").append(Integer.toString(amount)).append(" ").append(item.method_9781(amount, false).toTextComponent().applyFormat(TextFormat.DARK_RED)).append(".").applyFormat(TextFormat.GOLD), true);

		return amount;
	}
}
