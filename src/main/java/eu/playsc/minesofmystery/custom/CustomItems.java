package eu.playsc.minesofmystery.custom;

import eu.playsc.minesofmystery.abilities.Ability;
import eu.playsc.minesofmystery.abilities.EarthquakeAbility;
import eu.playsc.minesofmystery.abilities.FireballAbility;
import eu.playsc.minesofmystery.common.Color;
import eu.playsc.minesofmystery.common.Common;
import eu.playsc.minesofmystery.common.Message;
import eu.playsc.minesofmystery.custom.fonts.Caps;
import eu.playsc.minesofmystery.custom.internal.CustomItem;
import eu.playsc.minesofmystery.custom.manager.CustomItemManager;
import eu.playsc.minesofmystery.custom.util.CustomDisplay;
import eu.playsc.minesofmystery.custom.util.CustomTextures;
import eu.playsc.minesofmystery.dungeon.DungeonTeam;
import eu.playsc.minesofmystery.dungeon.handlers.DungeonHandler;
import eu.playsc.minesofmystery.model.PlayerData;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class CustomItems {
	public CustomItems() {
		this.registerAllCustomItems();
	}

	private void registerAllCustomItems() {
		final Field[] fields = this.getClass().getDeclaredFields();

		Arrays.stream(fields)
				.filter(field -> field.getType().equals(CustomItem.class))
				.forEach(field -> {
					try {
						final CustomItem customItem = (CustomItem) field.get(null);
						CustomItemManager.get().registerCustomItem(customItem);
					} catch (final IllegalAccessException e) {
						Common.error("Failed to register custom item", e);
					}
				});
	}

	public static final CustomItem POTION_T1 = new CustomItem(
			new NamespacedKey("minesofmystery", "potion_t1"),
			"minesofmystery/item/potion_t1",
			Material.HONEY_BOTTLE,
			new CustomDisplay(Component.text("Health Potion T1").color(Color.POTION)),
			new CustomTextures(),
			CustomItem.Action.ON_CONSUME,
			(event -> {
				final PlayerItemConsumeEvent consumeEvent = (PlayerItemConsumeEvent) event;
				final Player player = consumeEvent.getPlayer();

				player.setHealth(Math.min(player.getHealth() + 2, player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
			})
	);

	public static final CustomItem POTION_T2 = new CustomItem(
			new NamespacedKey("minesofmystery", "potion_t2"),
			"minesofmystery/item/potion_t2",
			Material.HONEY_BOTTLE,
			new CustomDisplay(Component.text("Health Potion T2").color(Color.POTION)),
			new CustomTextures(),
			CustomItem.Action.ON_CONSUME,
			(event -> {
				final PlayerItemConsumeEvent consumeEvent = (PlayerItemConsumeEvent) event;
				final Player player = consumeEvent.getPlayer();

				player.setHealth(Math.min(player.getHealth() + 4, player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
			})
	);

	public static final CustomItem POTION_T3 = new CustomItem(
			new NamespacedKey("minesofmystery", "potion_t3"),
			"minesofmystery/item/potion_t3",
			Material.HONEY_BOTTLE,
			new CustomDisplay(Component.text("Health Potion T3").color(Color.POTION)),
			new CustomTextures(),
			CustomItem.Action.ON_CONSUME,
			(event -> {
				final PlayerItemConsumeEvent consumeEvent = (PlayerItemConsumeEvent) event;
				final Player player = consumeEvent.getPlayer();

				player.setHealth(Math.min(player.getHealth() + 6, player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
			})
	);

	public static final CustomItem DUNGEON_KEY = new CustomItem(
			new NamespacedKey("minesofmystery", "dungeon_key"),
			"minesofmystery/item/dungeon_key",
			Material.CARROT_ON_A_STICK,
			new CustomDisplay(Component.text("Dungeon Key").color(Color.DUNGEON_KEY)),
			new CustomTextures(),
			CustomItem.Action.ON_RIGHT_CLICK,
			(e -> {
				PlayerInteractEvent event = (PlayerInteractEvent) e;
				if (!PlayerData.from(event.getPlayer()).isEnderDragonDefeated()) {
					event.getPlayer().sendActionBar(Caps.get("You are missing the required quest state to use this item!", Color.RED));
					return;
				}

				if (DungeonHandler.get().isInDungeon(event.getPlayer())) {
					Message.error(event.getPlayer(), "You are already in a dungeon!");
					return;
				}

				if (DungeonTeam.get(event.getPlayer()).getDungeon() != null) {
					Message.error(event.getPlayer(), "You can only have one open dungeon per team!");
					return;
				}

				if (DungeonTeam.get(event.getPlayer()).getLeader().getUuid() != event.getPlayer().getUniqueId()) {
					Message.error(event.getPlayer(), "Only your team leader may open new dungeons!");
					return;
				}

				event.setCancelled(true);
				DungeonHandler.get().createDungeon(event.getPlayer(), event.getItem());
				event.getItem().setAmount(event.getItem().getAmount() - 1);
			}),
			false
	);

	public static final CustomItem FIREBALL_ABILITY = new CustomItem(
			new NamespacedKey("minesofmystery", "fireball_ability"),
			"minesofmystery/item/fireball_ability",
			FireballAbility.MATERIAL,
			new CustomDisplay(
					Component.text("Fireball").color(Color.ABILITY_FIREBALL),
					List.of(
							Caps.get("Mana Cost: ", Color.GRAY).append(Caps.get(FireballAbility.MANA_COST, Color.AQUA)),
							Caps.get("Cooldown: ", Color.GRAY).append(Caps.get(FireballAbility.COOLDOWN, Color.YELLOW))
					)
			),
			new CustomTextures(),
			CustomItem.Action.ON_RIGHT_CLICK,
			(e -> Ability.triggerAbility(FireballAbility.KEY, ((PlayerInteractEvent) e).getPlayer()))
	);

	public static final CustomItem EARTHQUAKE_ABILITY = new CustomItem(
			new NamespacedKey("minesofmystery", "earthquake_ability"),
			"minesofmystery/item/earthquake_ability",
			EarthquakeAbility.MATERIAL,
			new CustomDisplay(
					Component.text("Earthquake").color(Color.ABILITY_EARTHQUAKE),
					List.of(
							Caps.get("Mana Cost: ", Color.GRAY).append(Caps.get(EarthquakeAbility.MANA_COST, Color.AQUA)),
							Caps.get("Cooldown: ", Color.GRAY).append(Caps.get(EarthquakeAbility.COOLDOWN, Color.YELLOW))
					)
			),
			new CustomTextures(),
			CustomItem.Action.ON_RIGHT_CLICK,
			(e -> {
				PlayerInteractEvent event = (PlayerInteractEvent) e;
				Ability.triggerAbility(EarthquakeAbility.KEY, event.getPlayer());
			})
	);
}
