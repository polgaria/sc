package site.geni.stuff.mixins;

import net.minecraft.server.command.ListCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings("unused")
@Mixin(ListCommand.class)
public class ListCommandVanillaMixin {
	@ModifyConstant(method = "register", constant = @Constant(stringValue = "list"))
	private static String comName(String original) {
		return "v" + original;
	}
}
