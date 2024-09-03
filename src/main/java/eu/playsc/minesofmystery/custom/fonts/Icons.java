package eu.playsc.minesofmystery.custom.fonts;

import eu.playsc.minesofmystery.custom.internal.CustomFont;
import lombok.NoArgsConstructor;

@SuppressWarnings("ALL")
@NoArgsConstructor
public class Icons extends CustomFont {
	public static final Icons TEAM = new Icons("\ue200", "minesofmystery:fonts/icons/team.png", 8, 7, 16, 16);
	public static final Icons HOURGLASS = new Icons("\ue201", "minesofmystery:fonts/icons/hourglass.png", 8, 7, 16, 16);

	public static final Icons DUNGEON = new Icons("\ue202", "minesofmystery:fonts/icons/dungeon.png", 8, 7, 16, 16);
	public static final Icons NO_DUNGEON = new Icons("\ue203", "minesofmystery:fonts/icons/no_dungeon.png", 8, 7, 16, 16);
	public static final Icons DUNGEON_GENERATING = new Icons("\ue204", "minesofmystery:fonts/icons/dungeon_generating.png", 8, 7, 16, 16);
	public static final Icons DUNGEON_COMPLETED = new Icons("\ue205", "minesofmystery:fonts/icons/dungeon_completed.png", 8, 7, 16, 16);

	// Healthbar
	public static final Icons HEALTHBAR_BORDER = new Icons("\uf200", "minesofmystery:fonts/icons/healthbar/border.png", 5, 5, 1, 5);
	public static final Icons HEALTHBAR_PASSIVE_FULL = new Icons("\uf201", "minesofmystery:fonts/icons/healthbar/passive_full.png", 5, 5, 1, 5);
	public static final Icons HEALTHBAR_PASSIVE_EMPTY = new Icons("\uf202", "minesofmystery:fonts/icons/healthbar/passive_empty.png", 5, 5, 1, 5);
	public static final Icons HEALTHBAR_HOSTILE_FULL = new Icons("\uf203", "minesofmystery:fonts/icons/healthbar/hostile_full.png", 5, 5, 1, 5);
	public static final Icons HEALTHBAR_HOSTILE_EMPTY = new Icons("\uf204", "minesofmystery:fonts/icons/healthbar/hostile_empty.png", 5, 5, 1, 5);
	public static final Icons HEALTHBAR_BOSS_FULL = new Icons("\uf205", "minesofmystery:fonts/icons/healthbar/boss_full.png", 5, 5, 1, 5);
	public static final Icons HEALTHBAR_BOSS_EMPTY = new Icons("\uf206", "minesofmystery:fonts/icons/healthbar/boss_empty.png", 5, 5, 1, 5);

	// Manabar
	public static final Icons MANABAR_BORDER_LEFT = new Icons("\ud200", "minesofmystery:fonts/icons/manabar/border-left.png", 15, 0, 2, 15);
	public static final Icons MANABAR_BORDER_RIGHT = new Icons("\ud201", "minesofmystery:fonts/icons/manabar/border-right.png", 15, 0, 2, 15);
	public static final Icons MANABAR_FULL = new Icons("\ud202", "minesofmystery:fonts/icons/manabar/full.png", 15, 0, 1, 15);
	public static final Icons MANABAR_EMPTY = new Icons("\ud203", "minesofmystery:fonts/icons/manabar/empty.png", 15, 0, 1, 15);

	// Transition
	public static final Icons TRANSITION_FULL = new Icons("\uf00d\uf00e", "minesofmystery:fonts/icons/transition_full.png", 256, 128, 512, 256);
	public static final Icons TRANSITION_TRANSPARENT = new Icons("\uf00f\uf010", "minesofmystery:fonts/icons/transition_transparent.png", 256, 128, 512, 256);

	private Icons(final String value, final String path, final int height, final int ascent, final int imageWidth, final int imageHeight) {
		super("icons", value, path, height, ascent, imageWidth, imageHeight);
	}
}
