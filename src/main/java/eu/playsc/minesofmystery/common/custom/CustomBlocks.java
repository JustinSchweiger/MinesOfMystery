package eu.playsc.minesofmystery.common.custom;

import eu.playsc.minesofmystery.common.Colors;
import eu.playsc.minesofmystery.common.custom.manager.CustomBlockManager;
import eu.playsc.minesofmystery.common.custom.util.CustomDisplay;
import eu.playsc.minesofmystery.common.custom.util.CustomSettings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;

import java.util.List;

public class CustomBlocks {
	public CustomBlocks() {
		CustomBlockManager.get().registerCustomBlock(METAL_BLOCK);
	}

	public static final CustomBlock METAL_BLOCK = new CustomBlock(
			NamespacedKey.fromString("minesofmystery:metal_block"),
			new CustomDisplay(Component.text("Metal Block").color(Colors.GRAY), List.of(Component.text("A block of metal", NamedTextColor.GRAY)), false),
			new CustomSettings(true, false, true),
			"minesofmystery:block/metal_block"
	);
}
