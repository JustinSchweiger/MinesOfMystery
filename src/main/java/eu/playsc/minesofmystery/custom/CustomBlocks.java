package eu.playsc.minesofmystery.custom;

import eu.playsc.minesofmystery.common.Color;
import eu.playsc.minesofmystery.common.Common;
import eu.playsc.minesofmystery.custom.internal.CustomBlock;
import eu.playsc.minesofmystery.custom.manager.CustomBlockManager;
import eu.playsc.minesofmystery.custom.util.CustomBlockSettings;
import eu.playsc.minesofmystery.custom.util.CustomDisplay;
import eu.playsc.minesofmystery.dungeon.DungeonTeam;
import eu.playsc.minesofmystery.dungeon.handlers.DungeonHandler;
import eu.playsc.minesofmystery.dungeon.loot.DungeonLoot;
import eu.playsc.minesofmystery.dungeon.loot.DungeonLootContext;
import eu.playsc.minesofmystery.dungeon.world.DungeonWorld;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class CustomBlocks {
	public CustomBlocks() {
		this.registerAllCustomBlocks();
	}

	private void registerAllCustomBlocks() {
		final Field[] fields = this.getClass().getDeclaredFields();

		Arrays.stream(fields)
				.filter(field -> field.getType().equals(CustomBlock.class))
				.forEach(field -> {
					try {
						final CustomBlock customBlock = (CustomBlock) field.get(null);
						CustomBlockManager.get().registerCustomBlock(customBlock);
					} catch (final IllegalAccessException e) {
						Common.error("Failed to register custom block", e);
					}
				});
	}

	public static boolean isCustomBlock(final Block block, final CustomBlock... customBlock) {
		final AtomicBoolean isCustomBlock = new AtomicBoolean(false);

		block.getLocation().getWorld().getNearbyEntities(block.getBoundingBox()).stream()
				.filter(entity -> entity instanceof ItemDisplay)
				.map(entity -> (ItemDisplay) entity)
				.findFirst()
				.ifPresent(itemDisplay -> {
					final ItemStack itemStack = itemDisplay.getItemStack();
					final String key = itemStack.getItemMeta().getPersistentDataContainer().get(CustomBlock.CUSTOM_BLOCK_DATA, PersistentDataType.STRING);
					if (key == null)
						return;

					final NamespacedKey namespacedKey = NamespacedKey.fromString(key);
					if (namespacedKey == null)
						return;

					isCustomBlock.set(Arrays.stream(customBlock).anyMatch(b -> b.getKey().equals(namespacedKey)));
				});

		return isCustomBlock.get();
	}

	public static final CustomBlock DUNGEON_PORTAL_EW = new CustomBlock(
			new NamespacedKey("minesofmystery", "dungeon_portal_ew"),
			new CustomDisplay(
					Component.text("Dungeon Portal").color(Color.PURPLE),
					List.of(Component.text("East/West").color(Color.GRAY)),
					true
			),
			new CustomBlockSettings(false, true, false, Material.NETHER_PORTAL.createBlockData()),
			"minesofmystery/block/dungeon_portal_ew",
			Sound.BLOCK_GLASS_BREAK,
			Sound.BLOCK_GLASS_PLACE
	);

	public static final CustomBlock DUNGEON_PORTAL_NS = new CustomBlock(
			NamespacedKey.fromString("minesofmystery:dungeon_portal_ns"),
			new CustomDisplay(Component.text("Dungeon Portal").color(Color.PURPLE), List.of(Component.text("North/South").color(Color.GRAY)), true),
			new CustomBlockSettings(false, true, false, Material.NETHER_PORTAL.createBlockData()),
			"minesofmystery/block/dungeon_portal_ns",
			Sound.BLOCK_GLASS_BREAK,
			Sound.BLOCK_GLASS_PLACE
	);

	public static final CustomBlock LOOT_CRATE = new CustomBlock(
			NamespacedKey.fromString("minesofmystery:loot_crate"),
			new CustomDisplay(Component.text("Loot Crate").color(Color.AQUA), List.of(), true),
			new CustomBlockSettings(false, false, false, Material.AMETHYST_CLUSTER.createBlockData()),
			"minesofmystery/block/loot_crate",
			Sound.BLOCK_AMETHYST_CLUSTER_BREAK,
			Sound.BLOCK_AMETHYST_CLUSTER_PLACE,
			(event, customBlock) -> {
				final Optional<DungeonWorld> dungeon = DungeonHandler.get().getDungeon(event.getPlayer().getWorld());
				if (dungeon.isEmpty())
					return;

				final DungeonTeam team = DungeonTeam.get(event.getPlayer());
				if (team.getDungeon() == null)
					return;

				final DungeonLootContext context = new DungeonLootContext(event.getClickedBlock().getLocation(), null, event.getPlayer());
				Collection<ItemStack> items = new DungeonLoot().populateChestLoot(new Random(), context);

				items.forEach(item -> event.getClickedBlock().getLocation().getWorld().dropItem(event.getClickedBlock().getLocation(), item));
				customBlock.breakBlock(event.getClickedBlock().getLocation(), false, false);
			}
	);
}
