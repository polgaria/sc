package site.geni.stuff.mixins;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Environment(EnvType.SERVER)
@Mixin(PlayerManager.class)
public class OnJoinMixin {
	@Inject(at = @At("RETURN"), method = "onPlayerConnect")
	private void onPlayerConnect(ClientConnection connection, ServerPlayerEntity entity, CallbackInfo info) {
		ServerPlayNetworkHandler playNetworkHandler = new ServerPlayNetworkHandler(entity.server, connection, entity);
		PlayerManager serverPlayerManager = entity.server.getPlayerManager();

		int playerCount = serverPlayerManager.getPlayerList().size();
		int maxPlayerCount = serverPlayerManager.getMaxPlayerCount();

		TextComponent onJoinMessage = new StringTextComponent(String.format("\u00a76There are \u00a74%d\u00a76 out of \u00a74%d\u00a76 maximum players online.", playerCount, maxPlayerCount));
		entity.addChatMessage(onJoinMessage, false);
	}
}
