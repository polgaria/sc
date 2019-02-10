package site.geni.stuff.mixins;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import site.geni.stuff.commands.TpaCommand;

import java.util.List;

@SuppressWarnings("unused")
@Environment(EnvType.SERVER)
@Mixin(ServerPlayNetworkHandler.class)
public class OnDisconnectMixin {
	@Shadow
	public ServerPlayerEntity player;

	@Inject(at = @At("RETURN"), method = "onConnectionLost")
	private void onConnectionLost(CallbackInfo info) {
		if (TpaCommand.getRequests().containsValue(player.getUuid())) {
			ServerPlayerEntity destPlayer = null;
			final List<ServerPlayerEntity> playerList = player.server.getPlayerManager().getPlayerList();

			/* search through all players on the server until the destination player is found */
			for (ServerPlayerEntity search : playerList) {
				if (search.getUuid().equals(TpaCommand.getRequests().inverse().get(player.getUuid()))) {
					destPlayer = search;
				}
			}

			if (destPlayer != null) {
				/* remove expired TPA request from requests */
				TpaCommand.getRequests().remove(player.getUuid());

				/* prepare for message */
				final TextComponent originPlayerName = new StringTextComponent(player.getDisplayName().getString()).applyFormat(TextFormat.DARK_RED);
				/* send message to destination player alerting them that the TPA request has expired */
				final TextComponent requestExpiredFromMessage = new StringTextComponent("TPA request from ").append(originPlayerName).append(" has expired.").applyFormat(TextFormat.GOLD);

				destPlayer.addChatMessage(requestExpiredFromMessage, false);
			}
		}
	}
}
