package eu.playsc.minesofmystery.custom.fonts;

import lombok.NoArgsConstructor;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;

@NoArgsConstructor
public class Spaces {
	public static Component negative(final int amount) {
		return Component.translatable("space.-" + amount).font(Key.key("minesofmystery", "spaces"));
	}

	public static Component positive(final int amount) {
		return Component.translatable("space." + amount).font(Key.key("minesofmystery", "spaces"));
	}

	public static Component newlayer() {
		return Component.translatable("newlayer").font(Key.key("minesofmystery", "spaces"));
	}

	public static Component get(final int amount) {
		return Component.translatable("space." + amount).font(Key.key("minesofmystery", "spaces"));
	}
}
