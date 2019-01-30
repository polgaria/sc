package site.geni.stuff.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.events.ServerEvent;
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
import net.minecraft.text.TranslatableTextComponent;

public class iCommand {
	public static void register() {
		ServerEvent.START.register(
				server -> server.getCommandManager().getDispatcher().register(
						ServerCommandManager.literal("i").requires(serverCommandSource ->
								serverCommandSource.hasPermissionLevel(2)
						).then(
								ServerCommandManager.argument("item", ItemStackArgumentType.create()).executes(
										commandContext_1 -> onCommand(commandContext_1, ItemStackArgumentType.method_9777(commandContext_1, "item"), 1)
								).then(
										ServerCommandManager.argument("count", IntegerArgumentType.integer(1)).executes(
												commandContext_1 -> onCommand(commandContext_1, ItemStackArgumentType.method_9777(commandContext_1, "item"), IntegerArgumentType.getInteger(commandContext_1, "count"))
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

		context.getSource().sendFeedback(new TranslatableTextComponent("\u00a76Gave you \u00a7a%s %s\u00a76.", amount, item.method_9781(amount, false).toTextComponent().applyFormat(TextFormat.DARK_RED)), true);

		return amount;
	}
}
