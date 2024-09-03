package eu.playsc.minesofmystery.abilities;

import org.bukkit.Material;

public interface Cooldownable {
	int getCooldown();

	Material getMaterial();
}
