package eu.playsc.minesofmystery.custom.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.ArrayList;
import java.util.List;


public class CustomDisplay {
	private final Component displayName;
	private final List<Component> lore;
	private final boolean italicLore;

	public CustomDisplay(final Component displayName, final List<Component> lore, final boolean italicLore) {
		this.displayName = displayName;
		this.lore = lore;
		this.italicLore = italicLore;
	}

	public CustomDisplay(final Component displayName, final List<Component> lore) {
		this(displayName, lore, false);
	}

	public CustomDisplay(final Component displayName) {
		this(displayName, List.of(), false);
	}

	public Component getDisplayName() {
		return this.displayName.decoration(TextDecoration.ITALIC, false);
	}

	public List<Component> getLore() {
		if (this.lore.isEmpty()) {
			return new ArrayList<>();
		}

		if (this.italicLore) {
			return this.lore;
		}

		return this.lore.stream().map(component -> component.decoration(TextDecoration.ITALIC, false)).toList();
	}
}
