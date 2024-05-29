package eu.playsc.minesofmystery.listeners;

import eu.playsc.minesofmystery.common.custom.CustomBlock;
import eu.playsc.minesofmystery.common.custom.manager.CustomBlockManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

@AutoRegister
public class CustomBlockListener implements Listener {
	@EventHandler
	public void onBlockPlace(final BlockPlaceEvent event) {
		final ItemStack item = event.getItemInHand();

		if (item.getItemMeta().hasCustomModelData() && item.getType().equals(Material.STONE)) {
			final Location location = event.getBlockPlaced().getLocation();
			final String key = item.getItemMeta().getPersistentDataContainer().get(CustomBlock.STORAGE_NAME, PersistentDataType.STRING);
			if (key == null) {
				event.setCancelled(true);
				return;
			}

			final NamespacedKey namespacedKey = NamespacedKey.fromString(key);
			CustomBlockManager.get().getCustomBlock(namespacedKey).ifPresent(customBlock -> customBlock.place(location));
		}
	}

	@EventHandler
	public void onBlockBreak(final PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND || event.getAction() != Action.LEFT_CLICK_BLOCK || event.getClickedBlock() == null)
			return;

		final Location location = event.getClickedBlock().getLocation();
		final Block block = location.getBlock();

		if (block.getType() != CustomBlock.SOLID_PLACEHOLDER && block.getType() != CustomBlock.NON_SOLID_PLACEHOLDER)
			return;

		location.getWorld().getNearbyEntities(event.getClickedBlock().getBoundingBox()).stream()
				.filter(entity -> entity instanceof ItemDisplay)
				.map(entity -> (ItemDisplay) entity)
				.findFirst()
				.ifPresent(itemDisplay -> {
					final ItemStack itemStack = itemDisplay.getItemStack();
					final String key = itemStack.getItemMeta().getPersistentDataContainer().get(CustomBlock.STORAGE_NAME, PersistentDataType.STRING);
					if (key == null) {
						return;
					}

					final NamespacedKey namespacedKey = NamespacedKey.fromString(key);
					CustomBlockManager.get().getCustomBlock(namespacedKey).ifPresent(customBlock -> {
						if (!customBlock.getSettings().isBreakable() && event.getPlayer().getGameMode() != GameMode.CREATIVE)
							return;

						customBlock.breakBlock(location, event.getPlayer().getGameMode().equals(GameMode.SURVIVAL));
						itemDisplay.remove();
					});
				});
	}
}
