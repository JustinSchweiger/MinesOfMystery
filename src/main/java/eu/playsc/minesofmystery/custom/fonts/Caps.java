package eu.playsc.minesofmystery.custom.fonts;

import eu.playsc.minesofmystery.common.Color;
import eu.playsc.minesofmystery.custom.internal.CustomFont;
import lombok.NoArgsConstructor;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@SuppressWarnings("unused")
public class Caps extends CustomFont {
	private static final List<Caps> LETTERS = new ArrayList<>();
	private static final List<Caps> DIGITS = new ArrayList<>();
	private static final List<Caps> SYMBOLS = new ArrayList<>();

	// Letters
	public static final Caps A = new Caps(Type.LETTER, "A", "minesofmystery:fonts/caps/letters/a.png", 5, 5, 5, 5);
	public static final Caps B = new Caps(Type.LETTER, "B", "minesofmystery:fonts/caps/letters/b.png", 5, 5, 5, 5);
	public static final Caps C = new Caps(Type.LETTER, "C", "minesofmystery:fonts/caps/letters/c.png", 5, 5, 5, 5);
	public static final Caps D = new Caps(Type.LETTER, "D", "minesofmystery:fonts/caps/letters/d.png", 5, 5, 5, 5);
	public static final Caps E = new Caps(Type.LETTER, "E", "minesofmystery:fonts/caps/letters/e.png", 5, 5, 5, 5);
	public static final Caps F = new Caps(Type.LETTER, "F", "minesofmystery:fonts/caps/letters/f.png", 5, 5, 5, 5);
	public static final Caps G = new Caps(Type.LETTER, "G", "minesofmystery:fonts/caps/letters/g.png", 5, 5, 5, 5);
	public static final Caps H = new Caps(Type.LETTER, "H", "minesofmystery:fonts/caps/letters/h.png", 5, 5, 5, 5);
	public static final Caps I = new Caps(Type.LETTER, "I", "minesofmystery:fonts/caps/letters/i.png", 5, 5, 3, 5);
	public static final Caps J = new Caps(Type.LETTER, "J", "minesofmystery:fonts/caps/letters/j.png", 5, 5, 5, 5);
	public static final Caps K = new Caps(Type.LETTER, "K", "minesofmystery:fonts/caps/letters/k.png", 5, 5, 4, 5);
	public static final Caps L = new Caps(Type.LETTER, "L", "minesofmystery:fonts/caps/letters/l.png", 5, 5, 5, 5);
	public static final Caps M = new Caps(Type.LETTER, "M", "minesofmystery:fonts/caps/letters/m.png", 5, 5, 5, 5);
	public static final Caps N = new Caps(Type.LETTER, "N", "minesofmystery:fonts/caps/letters/n.png", 5, 5, 5, 5);
	public static final Caps O = new Caps(Type.LETTER, "O", "minesofmystery:fonts/caps/letters/o.png", 5, 5, 5, 5);
	public static final Caps P = new Caps(Type.LETTER, "P", "minesofmystery:fonts/caps/letters/p.png", 5, 5, 5, 5);
	public static final Caps Q = new Caps(Type.LETTER, "Q", "minesofmystery:fonts/caps/letters/q.png", 5, 5, 5, 5);
	public static final Caps R = new Caps(Type.LETTER, "R", "minesofmystery:fonts/caps/letters/r.png", 5, 5, 5, 5);
	public static final Caps S = new Caps(Type.LETTER, "S", "minesofmystery:fonts/caps/letters/s.png", 5, 5, 5, 5);
	public static final Caps T = new Caps(Type.LETTER, "T", "minesofmystery:fonts/caps/letters/t.png", 5, 5, 5, 5);
	public static final Caps U = new Caps(Type.LETTER, "U", "minesofmystery:fonts/caps/letters/u.png", 5, 5, 5, 5);
	public static final Caps V = new Caps(Type.LETTER, "V", "minesofmystery:fonts/caps/letters/v.png", 5, 5, 5, 5);
	public static final Caps W = new Caps(Type.LETTER, "W", "minesofmystery:fonts/caps/letters/w.png", 5, 5, 5, 5);
	public static final Caps X = new Caps(Type.LETTER, "X", "minesofmystery:fonts/caps/letters/x.png", 5, 5, 5, 5);
	public static final Caps Y = new Caps(Type.LETTER, "Y", "minesofmystery:fonts/caps/letters/y.png", 5, 5, 5, 5);
	public static final Caps Z = new Caps(Type.LETTER, "Z", "minesofmystery:fonts/caps/letters/z.png", 5, 5, 5, 5);

	// Digits
	public static final Caps ZERO = new Caps(Type.DIGIT, "0", "minesofmystery:fonts/caps/digits/digit_0.png", 5, 5, 5, 5);
	public static final Caps ONE = new Caps(Type.DIGIT, "1", "minesofmystery:fonts/caps/digits/digit_1.png", 5, 5, 3, 5);
	public static final Caps TWO = new Caps(Type.DIGIT, "2", "minesofmystery:fonts/caps/digits/digit_2.png", 5, 5, 3, 5);
	public static final Caps THREE = new Caps(Type.DIGIT, "3", "minesofmystery:fonts/caps/digits/digit_3.png", 5, 5, 3, 5);
	public static final Caps FOUR = new Caps(Type.DIGIT, "4", "minesofmystery:fonts/caps/digits/digit_4.png", 5, 5, 3, 5);
	public static final Caps FIVE = new Caps(Type.DIGIT, "5", "minesofmystery:fonts/caps/digits/digit_5.png", 5, 5, 3, 5);
	public static final Caps SIX = new Caps(Type.DIGIT, "6", "minesofmystery:fonts/caps/digits/digit_6.png", 5, 5, 3, 5);
	public static final Caps SEVEN = new Caps(Type.DIGIT, "7", "minesofmystery:fonts/caps/digits/digit_7.png", 5, 5, 3, 5);
	public static final Caps EIGHT = new Caps(Type.DIGIT, "8", "minesofmystery:fonts/caps/digits/digit_8.png", 5, 5, 3, 5);
	public static final Caps NINE = new Caps(Type.DIGIT, "9", "minesofmystery:fonts/caps/digits/digit_9.png", 5, 5, 3, 5);

	// Symbols
	public static final Caps EXCLAMATION = new Caps(Type.SYMBOL, "!", "minesofmystery:fonts/caps/symbols/exclamation.png", 5, 5, 1, 5);
	public static final Caps AT = new Caps(Type.SYMBOL, "@", "minesofmystery:fonts/caps/symbols/at.png", 5, 5, 5, 5);
	public static final Caps HASH = new Caps(Type.SYMBOL, "#", "minesofmystery:fonts/caps/symbols/hash.png", 5, 5, 5, 5);
	public static final Caps BACKSLASH = new Caps(Type.SYMBOL, "\\", "minesofmystery:fonts/caps/symbols/backslash.png", 5, 5, 5, 5);
	public static final Caps BAR = new Caps(Type.SYMBOL, "|", "minesofmystery:fonts/caps/symbols/bar.png", 5, 5, 1, 5);
	public static final Caps COLON = new Caps(Type.SYMBOL, ":", "minesofmystery:fonts/caps/symbols/colon.png", 5, 5, 1, 5);
	public static final Caps COMMA = new Caps(Type.SYMBOL, ",", "minesofmystery:fonts/caps/symbols/comma.png", 5, 5, 1, 5);
	public static final Caps DOT = new Caps(Type.SYMBOL, ".", "minesofmystery:fonts/caps/symbols/dot.png", 5, 5, 1, 5);
	public static final Caps MINUS = new Caps(Type.SYMBOL, "-", "minesofmystery:fonts/caps/symbols/minus.png", 5, 5, 3, 5);
	public static final Caps PERCENT = new Caps(Type.SYMBOL, "%", "minesofmystery:fonts/caps/symbols/percent.png", 5, 5, 5, 5);
	public static final Caps PLUS = new Caps(Type.SYMBOL, "+", "minesofmystery:fonts/caps/symbols/plus.png", 5, 5, 3, 5);
	public static final Caps QUESTION = new Caps(Type.SYMBOL, "?", "minesofmystery:fonts/caps/symbols/question.png", 5, 5, 3, 5);
	public static final Caps SLASH = new Caps(Type.SYMBOL, "/", "minesofmystery:fonts/caps/symbols/slash.png", 5, 5, 5, 5);
	public static final Caps UNDERSCORE = new Caps(Type.SYMBOL, "_", "minesofmystery:fonts/caps/symbols/underscore.png", 5, 5, 3, 5);
	public static final Caps ROUND_BRACKET_LEFT = new Caps(Type.SYMBOL, "(", "minesofmystery:fonts/caps/symbols/round_bracket_left.png", 5, 5, 2, 5);
	public static final Caps ROUND_BRACKET_RIGHT = new Caps(Type.SYMBOL, ")", "minesofmystery:fonts/caps/symbols/round_bracket_right.png", 5, 5, 2, 5);
	public static final Caps SQUARE_BRACKET_LEFT = new Caps(Type.SYMBOL, "[", "minesofmystery:fonts/caps/symbols/square_bracket_left.png", 5, 5, 2, 5);
	public static final Caps SQUARE_BRACKET_RIGHT = new Caps(Type.SYMBOL, "]", "minesofmystery:fonts/caps/symbols/square_bracket_right.png", 5, 5, 2, 5);
	public static final Caps CURLY_BRACKET_LEFT = new Caps(Type.SYMBOL, "{", "minesofmystery:fonts/caps/symbols/curly_bracket_left.png", 5, 5, 3, 5);
	public static final Caps CURLY_BRACKET_RIGHT = new Caps(Type.SYMBOL, "}", "minesofmystery:fonts/caps/symbols/curly_bracket_right.png", 5, 5, 3, 5);

	private Caps(final Type type, final String value, final String path, final int height, final int ascent, final int imageWidth, final int imageHeight) {
		super("caps", value, path, height, ascent, imageWidth, imageHeight);

		switch (type) {
			case LETTER -> LETTERS.add(this);
			case DIGIT -> DIGITS.add(this);
			case SYMBOL -> SYMBOLS.add(this);
		}
	}

	public static Component get(final String text, final TextColor color) {
		return Component.text(text.toUpperCase()).color(color).font(Key.key("minesofmystery", "caps"));
	}

	public static Component get(final String text) {
		return get(text, Color.WHITE);
	}

	public static Component get(final int number, final TextColor color) {
		return get(String.valueOf(number), color);
	}

	public static Component get(final int number) {
		return get(number, Color.WHITE);
	}

	public static Component get(final long number, final TextColor color) {
		return get(String.valueOf(number), color);
	}

	public static Component get(final long number) {
		return get(number, Color.WHITE);
	}

	public static Map<CustomFont, CustomFont.Overwrite> getOverwrite(final Type type, final int height) {
		return getOverwrite(type, height, -999999);
	}

	public static Map<CustomFont, CustomFont.Overwrite> getOverwrite(final Type type, final long ascent) {
		return getOverwrite(type, -999999, ascent);
	}

	public static Map<CustomFont, CustomFont.Overwrite> getOverwrite(final Type type, final int height, final long ascent) {
		return switch (type) {
			case LETTER -> {
				final Map<CustomFont, CustomFont.Overwrite> overwrites = new HashMap<>();
				for (final Caps letter : LETTERS) {
					overwrites.put(letter, new Overwrite(height, ascent));
				}
				yield overwrites;
			}
			case DIGIT -> {
				final Map<CustomFont, CustomFont.Overwrite> overwrites = new HashMap<>();
				for (final Caps digit : DIGITS) {
					overwrites.put(digit, new Overwrite(height, ascent));
				}
				yield overwrites;
			}
			case SYMBOL -> {
				final Map<CustomFont, CustomFont.Overwrite> overwrites = new HashMap<>();
				for (final Caps symbol : SYMBOLS) {
					overwrites.put(symbol, new Overwrite(height, ascent));
				}
				yield overwrites;
			}
		};
	}

	public enum Type {
		LETTER,
		DIGIT,
		SYMBOL
	}
}
