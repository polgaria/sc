package site.geni.stuff.mixins;

import net.minecraft.server.command.TimeCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings("unused")
@Mixin(TimeCommand.class)
public class TimeCommandVanillaMixin {
	@ModifyConstant(method = "register", constant = @Constant(stringValue = "time", ordinal = 0))
	private static String comName(String original) {
		return "v" + original;
	}
}
