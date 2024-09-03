package eu.playsc.minesofmystery.custom.internal;

import eu.playsc.minesofmystery.annotations.AutoRegister;
import eu.playsc.minesofmystery.custom.manager.CustomItemManager;
import eu.playsc.minesofmystery.custom.util.CustomDisplay;
import eu.playsc.minesofmystery.custom.util.CustomTextures;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.function.Consumer;

@Getter
public class CustomItem extends Custom {
	public static final NamespacedKey CUSTOM_ITEM_DATA = new NamespacedKey("minesofmystery", "custom_item_data");

	private final Material material;
	private final CustomTextures customTextures;
	private final Action action;
	private final Consumer<? extends Event> eventConsumer;
	private final boolean giveable;

	@Setter
	private int customModelData;

	public CustomItem(final NamespacedKey key, final String model, final Material material, final CustomDisplay display) {
		this(key, model, material, display, null, Action.NONE, null, true);
	}

	public CustomItem(final NamespacedKey key, final String model, final Material material, final CustomDisplay display, final CustomTextures customTextures) {
		this(key, model, material, display, customTextures, Action.NONE, null, true);
	}

	public CustomItem(final NamespacedKey key, final String model, final Material material, final CustomDisplay display, final CustomTextures customTextures, final Action action, final Consumer<Event> eventConsumer) {
		this(key, model, material, display, customTextures, action, eventConsumer, true);
	}

	public CustomItem(final NamespacedKey key, final String model, final Material material, final CustomDisplay display, final CustomTextures customTextures, final boolean giveable) {
		this(key, model, material, display, customTextures, Action.NONE, null, giveable);
	}


	public CustomItem(final NamespacedKey key, final String model, final Material material, final CustomDisplay display, final CustomTextures customTextures, final Action action, final Consumer<Event> eventConsumer, final boolean giveable) {
		super(key, model, display);
		this.material = material;
		this.customTextures = customTextures;
		this.action = action;
		this.eventConsumer = eventConsumer;
		this.giveable = giveable;
	}

	@Override
	public ItemStack getItemStack() {
		final ItemStack itemStack = new ItemStack(this.material);
		final ItemMeta itemMeta = itemStack.getItemMeta();

		itemMeta.setCustomModelData(this.customModelData);
		itemMeta.getPersistentDataContainer().set(CUSTOM_ITEM_DATA, PersistentDataType.STRING, this.getKey().asString());
		itemMeta.displayName(this.getDisplay().getDisplayName());
		itemMeta.lore(this.getDisplay().getLore());

		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

	@Getter
	public enum Action {
		ON_RIGHT_CLICK(PlayerInteractEvent.class),
		ON_LEFT_CLICK(PlayerInteractEvent.class),
		ON_DROP(PlayerDropItemEvent.class),
		ON_PICKUP(PlayerAttemptPickupItemEvent.class),
		ON_CONSUME(PlayerItemConsumeEvent.class),
		NONE(null);

		private final Class<? extends Event> eventClass;

		Action(final Class<? extends Event> eventClass) {
			this.eventClass = eventClass;
		}
	}

	@AutoRegister
	public static class Listener implements org.bukkit.event.Listener {
		@EventHandler
		public static void onPlayerInteract(final PlayerInteractEvent event) {
			handleEvent(event, Action.ON_RIGHT_CLICK, Action.ON_LEFT_CLICK);
		}

		@EventHandler
		public static void onDrop(final PlayerDropItemEvent event) {
			handleEvent(event, Action.ON_DROP);
		}

		@EventHandler
		static void onPickup(final PlayerAttemptPickupItemEvent event) {
			handleEvent(event, Action.ON_PICKUP);
		}

		@EventHandler
		public static void onConsume(final PlayerItemConsumeEvent event) {
			handleEvent(event, Action.ON_CONSUME);
		}

		private static <T extends Event> void handleEvent(final T event, final Action... actions) {
			final ItemStack item = getItemFromEvent(event);
			if (item == null)
				return;

			final String customItemKey = item.getItemMeta().getPersistentDataContainer().get(CUSTOM_ITEM_DATA, PersistentDataType.STRING);
			if (customItemKey == null)
				return;

			final NamespacedKey key = NamespacedKey.fromString(customItemKey);
			final CustomItem customItem = CustomItemManager.get().getCustomItem(key).orElse(null);
			if (customItem == null || customItem.getEventConsumer() == null)
				return;

			for (final Action action : actions) {
				if (customItem.getAction() == action) {
					if (event instanceof PlayerInteractEvent) {
						if (action == Action.ON_RIGHT_CLICK && !((PlayerInteractEvent) event).getAction().isRightClick())
							continue;
						if (action == Action.ON_LEFT_CLICK && !((PlayerInteractEvent) event).getAction().isLeftClick())
							continue;
					}

					((Consumer<T>) customItem.getEventConsumer()).accept(event);
					break;
				}
			}
		}

		private static ItemStack getItemFromEvent(final Event event) {
			if (event instanceof PlayerInteractEvent) {
				return ((PlayerInteractEvent) event).getItem();
			} else if (event instanceof PlayerDropItemEvent) {
				return ((PlayerDropItemEvent) event).getItemDrop().getItemStack();
			} else if (event instanceof PlayerAttemptPickupItemEvent) {
				return ((PlayerAttemptPickupItemEvent) event).getItem().getItemStack();
			} else if (event instanceof PlayerItemConsumeEvent) {
				return ((PlayerItemConsumeEvent) event).getItem();
			}

			return null;
		}
	}
}
