package site.geni.stuff.mixins;

import com.google.common.collect.HashBiMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import site.geni.stuff.commands.TpaCommand;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
@Environment(EnvType.SERVER)
@Mixin(ServerPlayNetworkHandler.class)
public class OnDisconnectMixin {
	@Shadow
	public ServerPlayerEntity player;

	@Inject(at = @At("RETURN"), method = "onConnectionLost")
	private void onConnectionLost(CallbackInfo info) {
		final HashBiMap<UUID, UUID> requests = TpaCommand.getRequests();

		if (requests.containsValue(player.getUuid())) {
			ServerPlayerEntity destPlayer = null;
			final List<ServerPlayerEntity> playerList = player.server.getPlayerManager().getPlayerList();

			/* search through all players on the server until the destination player is found */
			for (ServerPlayerEntity search : playerList) {
				if (search.getUuid().equals(requests.inverse().get(player.getUuid()))) {
					destPlayer = search;
				}
			}

			/* remove expired TPA request from requests */
			requests.remove(player.getUuid());

			/* send messages to both players alerting them that the TPA request has expired */
			final TextComponent requestExpiredFromMessage = new StringTextComponent(String.format("\u00a7cTPA request from \u00a74%s\u00a7c expired.", player.getDisplayName().getText()));

			destPlayer.addChatMessage(requestExpiredFromMessage, false);
		}
	}
}
