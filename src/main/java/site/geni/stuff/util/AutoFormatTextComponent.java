package site.geni.stuff.util;

import net.minecraft.text.AbstractTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;

public class AutoFormatTextComponent extends AbstractTextComponent {
	private TextFormat[] textFormat;
	private String text;

	public AutoFormatTextComponent(final String text, final TextFormat... textFormat) {
		this.textFormat = textFormat;
		this.text = text;

		this.applyFormat(this.textFormat);
	}

	public AutoFormatTextComponent(final TextComponent textComponent, final TextFormat... textFormat) {
		this.children.addAll(textComponent.getChildren());
		this.text = textComponent.getText();

		this.applyFormat(textFormat);
	}

	private TextComponent getTextComponent() {
		return new AutoFormatTextComponent(this, this.textFormat);
	}

	public String getText() {
		return this.text;
	}

	public TextComponent copyShallow() {
		return this.getTextComponent();
	}

}
