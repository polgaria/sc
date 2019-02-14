package site.geni.stuff.util;

import net.minecraft.text.AbstractTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;

public class AutoAppendTextComponent extends AbstractTextComponent {
	public AutoAppendTextComponent(Object... objects) {
		this.appendObjects(objects);
	}

	public AutoAppendTextComponent(TextFormat textFormat, Object... objects) {
		this.appendObjects(objects);

		this.applyFormat(textFormat);
		System.out.println(this.getString());
	}

	private void appendObjects(Object... objects) {
		for (Object obj : objects) {
			if (obj instanceof TextComponent) {
				this.append((TextComponent) obj);
			} else if (obj instanceof String) {
				this.append((String) obj);
			} else if (obj instanceof Integer) {
				this.append(Integer.toString((Integer) obj));
			} else {
				this.append(String.format("ERROR - %s is not a supported object!", obj.getClass().getName()));
			}
		}
	}

	/* yeah.. */
	public String getText() {
		return "";
	}

	private AutoAppendTextComponent getTextComponent() {
		return new AutoAppendTextComponent(this.children);
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof AutoAppendTextComponent)) {
			return false;
		} else {
			AutoAppendTextComponent autoAppendTextComponent = (AutoAppendTextComponent) object;
			return this.getChildren().equals(autoAppendTextComponent.getChildren()) && super.equals(object);
		}
	}

	public AutoAppendTextComponent copyShallow() {
		return this.getTextComponent();
	}
}
