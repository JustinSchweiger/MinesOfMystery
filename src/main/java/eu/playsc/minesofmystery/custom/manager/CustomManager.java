package eu.playsc.minesofmystery.custom.manager;

import eu.playsc.minesofmystery.custom.internal.CustomBlock;
import eu.playsc.minesofmystery.custom.internal.CustomItem;
import lombok.Getter;
import org.bukkit.NamespacedKey;

import java.util.*;

@Getter
public abstract class CustomManager {
	private static int itemCMDStart = 1000000;
	private static int blockCMDStart = 1000000;

	private final Map<NamespacedKey, CustomItem> customItems = new HashMap<>();
	private final Map<NamespacedKey, CustomBlock> customBlocks = new HashMap<>();

	// Static list of all instances
	private static final List<CustomManager> instances = new ArrayList<>();

	public CustomManager() {
		instances.add(this);
	}

	public void registerCustomItem(final CustomItem item) {
		item.setCustomModelData(itemCMDStart++);
		this.customItems.put(item.getKey(), item);
	}

	public void registerCustomBlock(final CustomBlock block) {
		block.setCustomModelData(blockCMDStart++);
		this.customBlocks.put(block.getKey(), block);
	}

	public List<String> getRegisteredItems() {
		return List.copyOf(
				this.customItems.keySet().stream()
						.filter(key -> this.customItems.get(key).isGiveable())
						.map(NamespacedKey::asString)
						.toList()
		);
	}

	public List<String> getRegisteredBlocks() {
		return List.copyOf(this.customBlocks.keySet().stream().map(NamespacedKey::asString).toList());
	}

	public Optional<CustomItem> getCustomItem(final NamespacedKey key) {
		return Optional.ofNullable(this.customItems.get(key));
	}

	public Optional<CustomBlock> getCustomBlock(final NamespacedKey key) {
		return Optional.ofNullable(this.customBlocks.get(key));
	}

	public static void generateAllModelFiles() {
		for (final CustomManager instance : instances) {
			instance.generateModelFiles();
		}
	}

	public abstract void generateModelFiles();
}
