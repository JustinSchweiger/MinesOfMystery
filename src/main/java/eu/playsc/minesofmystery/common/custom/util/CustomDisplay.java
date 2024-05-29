package eu.playsc.minesofmystery.common.custom.util;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.List;


public class CustomDisplay {
	@Getter
	private final Component displayName;
	private final List<Component> lore;
	private final boolean italicLore;

	public CustomDisplay(final Component displayName, final List<Component> lore, final boolean italicLore) {
		this.displayName = displayName;
		this.lore = lore;
		this.italicLore = italicLore;
	}

	public List<Component> getLore() {
		if (this.italicLore) {
			return this.lore;
		}

		return this.lore.stream().map(component -> component.decoration(TextDecoration.ITALIC, false)).toList();
	}
}
