package eu.playsc.minesofmystery.custom.internal;

import eu.playsc.minesofmystery.custom.util.CustomDisplay;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

@Getter
public abstract class Custom {
	private final NamespacedKey key;
	private final String model;
	private final CustomDisplay display;
	@Setter
	private int customModelData;

	public Custom(NamespacedKey key, String model, CustomDisplay display) {
		this.key = key;
		this.model = model;
		this.display = display;
	}

	public abstract ItemStack getItemStack();
}
