package eu.playsc.minesofmystery.custom.internal;

import eu.playsc.minesofmystery.annotations.AutoRegister;
import eu.playsc.minesofmystery.custom.manager.CustomBlockManager;
import eu.playsc.minesofmystery.custom.util.CustomBlockSettings;
import eu.playsc.minesofmystery.custom.util.CustomDisplay;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.function.BiConsumer;

@Getter
public class CustomBlock extends Custom {
	public static final Material SOLID_PLACEHOLDER = Material.BARRIER;
	public static final Material NON_SOLID_PLACEHOLDER = Material.STRUCTURE_VOID;
	public static final NamespacedKey CUSTOM_BLOCK_DATA = new NamespacedKey("minesofmystery", "custom_block_data");

	private final CustomBlockSettings settings;
	private final Sound breakSound;
	private final Sound placeSound;

	private final BiConsumer<PlayerInteractEvent, CustomBlock> rightClickConsumer;

	@Setter
	private int customModelData;

	public CustomBlock(final NamespacedKey key, final CustomDisplay display, final CustomBlockSettings settings, final String model, final Sound breakSound, final Sound placeSound, final BiConsumer<PlayerInteractEvent, CustomBlock> rightClickConsumer) {
		super(key, model, display);
		this.settings = settings;
		this.breakSound = breakSound;
		this.placeSound = placeSound;
		this.rightClickConsumer = rightClickConsumer;
	}

	public CustomBlock(final NamespacedKey key, final CustomDisplay display, final CustomBlockSettings settings, final String model, final Sound breakSound, final Sound placeSound) {
		this(key, display, settings, model, breakSound, placeSound, null);
	}

	@Override
	public ItemStack getItemStack() {
		final ItemStack itemStack = new ItemStack(Material.STONE);
		final ItemMeta itemMeta = itemStack.getItemMeta();

		itemMeta.setCustomModelData(this.customModelData);
		itemMeta.getPersistentDataContainer().set(CUSTOM_BLOCK_DATA, PersistentDataType.STRING, this.getKey().asString());
		itemMeta.displayName(this.getDisplay().getDisplayName());
		itemMeta.lore(this.getDisplay().getLore());

		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

	public void place(final Location location) {
		final World world = location.getWorld();
		world.setBlockData(
				location,
				this.settings.isSolid() ?
						SOLID_PLACEHOLDER.createBlockData() :
						NON_SOLID_PLACEHOLDER.createBlockData()
		);

		world.playSound(location.toCenterLocation(), this.placeSound, SoundCategory.BLOCKS, 1, 1);
		world.spawn(location.toCenterLocation(), ItemDisplay.class, entity -> {
			entity.setItemStack(this.getItemStack());
			entity.setPersistent(true);

			if (this.settings.isGlowing())
				entity.setGlowing(true);
		});
	}

	public void breakBlock(final Location location, final boolean dropBlock, final boolean playSound) {
		final World world = location.getWorld();

		if (playSound)
			world.playSound(location.toCenterLocation(), this.breakSound, SoundCategory.BLOCKS, 1, 1);

		if (dropBlock)
			world.dropItemNaturally(location.toCenterLocation(), this.getItemStack());

		world.spawnParticle(Particle.BLOCK_CRACK, location.toCenterLocation(), 10, 0.3, 0.3, 0.3, 0.1, this.settings.getBreakParticle());
		world.setBlockData(location, Material.AIR.createBlockData());
		world.getNearbyEntities(location.toCenterLocation(), 0.5, 0.5, 0.5).forEach(entity -> {
			if (entity instanceof ItemDisplay) {
				entity.remove();
			}
		});
	}

	@AutoRegister
	public static class Listener implements org.bukkit.event.Listener {
		@EventHandler
		public static void onBlockPlace(final BlockPlaceEvent event) {
			final ItemStack item = event.getItemInHand();

			if (item.getItemMeta().hasCustomModelData() && item.getType().equals(Material.STONE)) {
				final Location location = event.getBlockPlaced().getLocation();
				final String key = item.getItemMeta().getPersistentDataContainer().get(CustomBlock.CUSTOM_BLOCK_DATA, PersistentDataType.STRING);
				if (key == null) {
					event.setCancelled(true);
					return;
				}

				final NamespacedKey namespacedKey = NamespacedKey.fromString(key);
				CustomBlockManager.get().getCustomBlock(namespacedKey).ifPresent(customBlock -> customBlock.place(location));
			}
		}

		@EventHandler
		public static void onBlockBreak(final PlayerInteractEvent event) {
			if (event.getHand() != EquipmentSlot.HAND || event.getClickedBlock() == null || (event.getAction() != Action.RIGHT_CLICK_BLOCK) && (event.getAction() != Action.LEFT_CLICK_BLOCK))
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
						final String key = itemStack.getItemMeta().getPersistentDataContainer().get(CustomBlock.CUSTOM_BLOCK_DATA, PersistentDataType.STRING);
						if (key == null) {
							return;
						}

						final NamespacedKey namespacedKey = NamespacedKey.fromString(key);
						CustomBlockManager.get().getCustomBlock(namespacedKey).ifPresent(customBlock -> {
							if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
								if (!customBlock.getSettings().isBreakable() && event.getPlayer().getGameMode() != GameMode.CREATIVE)
									return;

								customBlock.breakBlock(location, event.getPlayer().getGameMode().equals(GameMode.SURVIVAL), true);
								itemDisplay.remove();
							} else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
								if (customBlock.getRightClickConsumer() != null)
									customBlock.getRightClickConsumer().accept(event, customBlock);
							}
						});
					});
		}
	}
}
