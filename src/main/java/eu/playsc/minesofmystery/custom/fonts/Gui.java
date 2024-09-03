package eu.playsc.minesofmystery.custom.fonts;

import eu.playsc.minesofmystery.custom.internal.CustomFont;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@SuppressWarnings("unused")
public class Gui extends CustomFont {
	public static final Gui HEADER = new Gui("\ue001", "minesofmystery:fonts/guis/header.png", 256, 13, 198);

	public static final Gui BLANK_1 = new Gui("\ue003", "minesofmystery:fonts/guis/blank_1.png", 256, 13, 176);
	public static final Gui BLANK_2 = new Gui("\ue004", "minesofmystery:fonts/guis/blank_2.png", 256, 13, 176);
	public static final Gui BLANK_3 = new Gui("\ue005", "minesofmystery:fonts/guis/blank_3.png", 256, 13, 176);
	public static final Gui BLANK_4 = new Gui("\ue006", "minesofmystery:fonts/guis/blank_4.png", 256, 13, 176);
	public static final Gui BLANK_5 = new Gui("\ue007", "minesofmystery:fonts/guis/blank_5.png", 256, 13, 176);
	public static final Gui BLANK_6 = new Gui("\ue008", "minesofmystery:fonts/guis/blank_6.png", 256, 13, 176);

	private Gui(final String value, final String path, final int height, final int ascent, final int guiWidth) {
		super("gui", value, path, height, ascent, guiWidth, 0);
	}
}
