package eu.playsc.minesofmystery.common;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public enum Symbol {
	BAR('│'),
	THIN_BAR('┃'),
	UPPER_CORNER('┌'),
	MIDDLE_CORNER('├'),
	LOWER_CORNER('└'),
	PICKAXE('⛏'),
	TIME('⌚'),
	COINS('◎'),
	TOKENS('☄'),
	STONES('⧈'),
	HOURGLASS('⌛'),
	ERROR('❌'),
	WARNING('⚠'),
	SUCCESS('✔'),
	INFO('ℹ');

	private final char symbol;

	Symbol(final char symbol) {
		this.symbol = symbol;
	}

	public Component get(final TextColor color) {
		return Component.text(this.symbol + " ").color(color);
	}

	public Component get() {
		return this.get(Color.WHITE);
	}

	public Component get(final TextColor color, final String text) {
		return Component.text(this.symbol + " " + text).color(color);
	}

	public Component get(final TextColor color, final Component text) {
		return Component.text(this.symbol + " ").color(color).append(text);
	}

	public Component get(final String text) {
		return this.get(Color.WHITE, text);
	}

	public Component getShifted(final TextColor color, final String text) {
		return Component.text(text + " " + this.symbol).color(color);
	}

	public Component getShifted(final String text) {
		return this.getShifted(Color.WHITE, text);
	}

	public Component getNoSpace(final TextColor color, final String text) {
		return Component.text(this.symbol + text).color(color);
	}

	public Component getNoSpace(final String text) {
		return this.getNoSpace(Color.WHITE, text);
	}

	public String getSymbol() {
		return String.valueOf(this.symbol);
	}
}
