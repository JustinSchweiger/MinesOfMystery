package eu.playsc.minesofmystery.common;

import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class Color {
	// ########################################################################################################################
	//                                                    DEFAULT COLORS
	// ########################################################################################################################
	public static final TextColor WHITE = TextColor.color(0xFFFFFF);
	public static final TextColor BLACK = TextColor.color(0x000000);
	public static final TextColor RED = TextColor.color(0xFF5653);
	public static final TextColor GREEN = TextColor.color(0x65FF6E);
	public static final TextColor BLUE = TextColor.color(0x4F6BFF);
	public static final TextColor YELLOW = TextColor.color(0xFFD166);
	public static final TextColor PURPLE = TextColor.color(0x9A4DFF);
	public static final TextColor AQUA = TextColor.color(0x4DFFD6);
	public static final TextColor GRAY = TextColor.color(0xADADAD);
	public static final TextColor DARK_GRAY = TextColor.color(0x4D4D4D);
	public static final TextColor ORANGE = TextColor.color(0xFFAF62);


	// ########################################################################################################################
	//                                                    FORMATTER COLORS
	// ########################################################################################################################
	public static final TextColor WORLD = TextColor.color(0x4DFFD6);
	public static final TextColor X = TextColor.color(0xFFC325);
	public static final TextColor Y = TextColor.color(0xFFDB5B);
	public static final TextColor Z = TextColor.color(0xFFF293);
	public static final TextColor DAYS = TextColor.color(0x4F6BFF);
	public static final TextColor HOURS = TextColor.color(0x5B8DFF);
	public static final TextColor MINUTES = TextColor.color(0x6FA3FF);
	public static final TextColor SECONDS = TextColor.color(0x8AB8FF);
	public static final TextColor LOW_TIME_DAYS = TextColor.color(0xFF3D25);
	public static final TextColor LOW_TIME_HOURS = TextColor.color(0xFF5B3D);
	public static final TextColor LOW_TIME_MINUTES = TextColor.color(0xFF6F5B);
	public static final TextColor LOW_TIME_SECONDS = TextColor.color(0xFF8A6F);


	// ########################################################################################################################
	//                                                    COMMON COLORS
	// ########################################################################################################################
	public static final TextColor SUCCESS = TextColor.color(0xABD1AB);
	public static final TextColor SUCCESS_SYMBOL = TextColor.color(0x89FF7F);

	public static final TextColor ERROR = TextColor.color(0xD1ADB0);
	public static final TextColor ERROR_SYMBOL = TextColor.color(0xFF5356);

	public static final TextColor HARD_ERROR = TextColor.color(0xFF5D58);
	public static final TextColor HARD_ERROR_SYMBOL = TextColor.color(0xFF0000);

	public static final TextColor WARNING = TextColor.color(0xFFEDBB);
	public static final TextColor WARNING_SYMBOL = TextColor.color(0xFFC761);

	public static final TextColor INFO = TextColor.color(0x9CA2FF);
	public static final TextColor INFO_SYMBOL = TextColor.color(0x4F6BFF);

	public static final TextColor PROMPT = TextColor.color(0x6EE7FF);


	// ########################################################################################################################
	//                                                    ITEM COLORS
	// ########################################################################################################################
	public static final TextColor POTION = TextColor.color(0xFE2C01);
	public static final TextColor DUNGEON_KEY = TextColor.color(0x844D1B);


	// ########################################################################################################################
	//                                                    THEME COLORS
	// ########################################################################################################################
	public static final TextColor DRIPSTONE_CAVES = TextColor.color(0x453431);


	// ########################################################################################################################
	//                                                   ABILITY COLORS
	// ########################################################################################################################
	public static final TextColor ABILITY_DASH = TextColor.color(0xC4D9FF);
	public static final TextColor ABILITY_FIREBALL = TextColor.color(0xFF7D2D);
	public static final TextColor ABILITY_EARTHQUAKE = TextColor.color(0x8B4513);


	// ########################################################################################################################
	//                                                  OBJECTIVE COLORS
	// ########################################################################################################################
	public static final @Nullable TextColor KILL_MONSTERS_OBJECTIVE = TextColor.color(0xFF9476);
}
