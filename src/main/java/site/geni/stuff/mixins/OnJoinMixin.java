package site.geni.stuff.mixins;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import site.geni.stuff.commands.TimeCommand;

@SuppressWarnings("unused")
@Environment(EnvType.SERVER)
@Mixin(PlayerManager.class)
public class OnJoinMixin {
	@Inject(at = @At("RETURN"), method = "onPlayerConnect")
	private void onPlayerConnect(ClientConnection connection, ServerPlayerEntity entity, CallbackInfo info) {
		final ServerPlayNetworkHandler playNetworkHandler = new ServerPlayNetworkHandler(entity.server, connection, entity);
		final PlayerManager serverPlayerManager = entity.server.getPlayerManager();

		final TextComponent playerCount = new StringTextComponent(Integer.toString(serverPlayerManager.getPlayerList().size())).applyFormat(TextFormat.DARK_RED);
		final TextComponent maxPlayerCount = new StringTextComponent(Integer.toString(serverPlayerManager.getMaxPlayerCount())).applyFormat(TextFormat.DARK_RED);

		final long timeOfDay = entity.world.getTimeOfDay();
		final TextComponent date = new StringTextComponent(TimeCommand.getTime(timeOfDay).getTime().toString()).applyFormat(TextFormat.GREEN);
		final TextComponent timeOfDayText = new StringTextComponent(Long.toString(timeOfDay)).applyFormat(TextFormat.GREEN);

		final TextComponent playersMessage = new StringTextComponent("There are ").append(playerCount).append(" out of ").append(maxPlayerCount).append(" maximum players online.").applyFormat(TextFormat.GOLD);
		final TextComponent dateAndTimeMessage = new StringTextComponent("The date and time is ").append(date).append(" (").append(timeOfDayText).append(")").applyFormat(TextFormat.GOLD);

		entity.addChatMessage(playersMessage, false);
		entity.addChatMessage(dateAndTimeMessage, false);
	}
}
