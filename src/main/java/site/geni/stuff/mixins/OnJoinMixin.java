package site.geni.stuff.mixins;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import site.geni.stuff.commands.TimeCommand;
import site.geni.stuff.util.AutoAppendTextComponent;
import site.geni.stuff.util.AutoFormatTextComponent;

@SuppressWarnings("unused")
@Environment(EnvType.SERVER)
@Mixin(PlayerManager.class)
public class OnJoinMixin {
	@Inject(at = @At("RETURN"), method = "onPlayerConnect")
	private void onPlayerConnect(ClientConnection connection, ServerPlayerEntity entity, CallbackInfo info) {
		final ServerPlayNetworkHandler playNetworkHandler = new ServerPlayNetworkHandler(entity.server, connection, entity);
		final PlayerManager serverPlayerManager = entity.server.getPlayerManager();

		final TextComponent playerCount = new AutoFormatTextComponent(Integer.toString(serverPlayerManager.getPlayerList().size()), TextFormat.DARK_RED);
		final TextComponent maxPlayerCount = new AutoFormatTextComponent(Integer.toString(serverPlayerManager.getMaxPlayerCount()), TextFormat.DARK_RED);


		final long timeOfDay = entity.world.getTimeOfDay();
		final TextComponent date = new AutoFormatTextComponent(TimeCommand.getTime(timeOfDay).getTime().toString(), TextFormat.GREEN);
		final TextComponent timeOfDayText = new AutoFormatTextComponent(Long.toString(timeOfDay), TextFormat.GREEN);


		final TextComponent playersMessage = new AutoAppendTextComponent(TextFormat.GOLD, "There are ", playerCount, " out of ", maxPlayerCount, " maximum players online.");
		final TextComponent dateAndTimeMessage = new AutoAppendTextComponent(TextFormat.GOLD, "The date and time is ", date, " (", timeOfDayText, ")");


		entity.addChatMessage(playersMessage, false);
		entity.addChatMessage(dateAndTimeMessage, false);
	}
}
