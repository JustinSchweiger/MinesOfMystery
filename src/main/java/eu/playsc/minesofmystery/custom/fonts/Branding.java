package eu.playsc.minesofmystery.custom.fonts;

import eu.playsc.minesofmystery.common.Color;
import eu.playsc.minesofmystery.custom.internal.CustomFont;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;

@NoArgsConstructor
public class Branding extends CustomFont {
	public static final Branding LOGO = new Branding(
			"\ue201\ue202\ue203\ue204",
			"minesofmystery:fonts/branding/logo.png",
			16,
			9
	);

	private Branding(final String character, final String path, final int height, final int ascent) {
		super("branding", character, path, height, ascent, 0, 0);
	}

	@Override
	public Component get() {
		final String[] characters = this.getCharacter().split("");

		Component component = Component.empty();
		for (final String character : characters) {
			component = component
					.append(Spaces.negative(1))
					.append(Component.text(character).color(Color.WHITE).font(this.getFontKey()));
		}

		return component;
	}
}

