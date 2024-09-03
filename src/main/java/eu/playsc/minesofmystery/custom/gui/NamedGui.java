package eu.playsc.minesofmystery.custom.gui;

import eu.playsc.minesofmystery.custom.fonts.Caps;
import eu.playsc.minesofmystery.custom.fonts.Gui;
import eu.playsc.minesofmystery.custom.fonts.Spaces;
import eu.playsc.minesofmystery.custom.internal.CustomFont;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

import java.util.HashMap;

public abstract class NamedGui {
	private final String title;
	private final TextColor titleColor;
	private final Gui guiBackground;

	public NamedGui(final String title, final TextColor titleColor, final Gui guiBackground) {
		this.title = title;
		this.titleColor = titleColor;
		this.guiBackground = guiBackground;
	}

	public NamedGui(final String title, final TextColor titleColor) {
		this(title, titleColor, null);
	}

	public Component getTitle() {
		final CustomFont.CountResult result = CustomFont.countCharacters(this.title.toUpperCase());
		final int width = result.calculateWidth(new HashMap<>());

		final int totalWidth = Gui.HEADER.getImageWidth();
		final int offset = (totalWidth - width) / 2;

		if (this.guiBackground == null) {
			return Spaces.negative(19)
					.append(Gui.HEADER.get())
					.append(Spaces.negative(width + offset - 2))
					.append(Caps.get(this.title, this.titleColor));
		}

		return Spaces.negative(8)
				.append(this.guiBackground.get())
				.append(Spaces.negative(this.guiBackground.getImageWidth() + 12))
				.append(Gui.HEADER.get())
				.append(Spaces.negative(width + offset - 2))
				.append(Caps.get(this.title, this.titleColor));
	}
}
