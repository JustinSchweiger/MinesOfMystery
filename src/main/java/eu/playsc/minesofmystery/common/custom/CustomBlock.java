package eu.playsc.minesofmystery.common.custom;

import eu.playsc.minesofmystery.common.custom.util.CustomDisplay;
import eu.playsc.minesofmystery.common.custom.util.CustomSettings;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

@Getter
public class CustomBlock {
	public static final Material SOLID_PLACEHOLDER = Material.BARRIER;
	public static final Material NON_SOLID_PLACEHOLDER = Material.STRUCTURE_VOID;
	public static final NamespacedKey STORAGE_NAME = new NamespacedKey("minesofmystery", "custom_block_data");

	private final NamespacedKey key;
	private final CustomDisplay display;
	private final CustomSettings settings;
	private final String model;
	private final Sound breakSound;
	private final Sound placeSound;

	@Setter
	private int customModelData;

	public CustomBlock(final NamespacedKey key, final CustomDisplay display, final CustomSettings settings, final String model, final Sound breakSound, final Sound placeSound) {
		this.key = key;
		this.display = display;
		this.settings = settings;
		this.model = model;
		this.breakSound = breakSound;
		this.placeSound = placeSound;
	}

	public CustomBlock(final NamespacedKey key, final CustomDisplay display, final CustomSettings settings, final String model) {
		this(key, display, settings, model, Sound.BLOCK_STONE_BREAK, Sound.BLOCK_STONE_PLACE);
	}

	public ItemStack getItemStack() {
		final ItemStack itemStack = new ItemStack(Material.STONE);
		final ItemMeta itemMeta = itemStack.getItemMeta();

		itemMeta.setCustomModelData(this.customModelData);
		itemMeta.getPersistentDataContainer().set(STORAGE_NAME, PersistentDataType.STRING, this.key.asString());
		itemMeta.displayName(this.display.getDisplayName());
		itemMeta.lore(this.display.getLore());

		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

	public void place(final Location location) {
		final World world = location.getWorld();
		world.setBlockData(location, this.settings.isSolid() ? SOLID_PLACEHOLDER.createBlockData() : NON_SOLID_PLACEHOLDER.createBlockData());

		world.playSound(this.getLocationFromBlock(location), this.placeSound, SoundCategory.BLOCKS, 1, 1);
		world.spawn(this.getLocationFromBlock(location), ItemDisplay.class, entity -> {
			entity.setItemStack(this.getItemStack());
			entity.setPersistent(true);
			entity.setInvulnerable(true);

			if (this.settings.isGlowing())
				entity.setGlowing(true);
		});
	}

	public void breakBlock(final Location location, final boolean dropBlock) {
		final World world = location.getWorld();
		world.playSound(this.getLocationFromBlock(location), this.breakSound, SoundCategory.BLOCKS, 1, 1);

		if (dropBlock)
			world.dropItemNaturally(this.getLocationFromBlock(location), this.getItemStack());

		world.spawnParticle(Particle.BLOCK_CRACK, this.getLocationFromBlock(location), 10, 0.3, 0.3, 0.3, 0.1, Material.STONE.createBlockData());
		world.setBlockData(location, Material.AIR.createBlockData());
		world.getNearbyEntities(this.getLocationFromBlock(location), 0.5, 0.5, 0.5).forEach(entity -> {
			if (entity instanceof ItemDisplay) {
				entity.remove();
			}
		});
	}

	private Location getLocationFromBlock(final Location location) {
		final Location entityLocation = location.clone();
		entityLocation.setX(entityLocation.getBlockX() + 0.5);
		entityLocation.setY(entityLocation.getBlockY() + 0.5);
		entityLocation.setZ(entityLocation.getBlockZ() + 0.5);

		return entityLocation;
	}
}
