package eu.playsc.minesofmystery.common;

import lombok.Getter;

@Getter
public enum LogLevel {
	INFO("§9"),
	SUCCESS("§a"),
	WARNING("§e"),
	ERROR("§c"),
	EXCEPTION("§4"),
	DEBUG("§7");

	private final String color;

	LogLevel(final String color) {
		this.color = color;
	}
}
