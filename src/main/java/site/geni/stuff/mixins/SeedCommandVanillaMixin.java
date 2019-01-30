package site.geni.stuff.mixins;

import net.minecraft.server.command.SeedCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings("unused")
@Mixin(SeedCommand.class)
public class SeedCommandVanillaMixin {
	@ModifyConstant(method = "register", constant = @Constant(stringValue = "seed"))
	private static String comName(String original) {
		return "v" + original;
	}
}
