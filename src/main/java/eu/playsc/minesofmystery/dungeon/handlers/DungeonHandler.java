package eu.playsc.minesofmystery.dungeon.handlers;

import eu.playsc.minesofmystery.common.Color;
import eu.playsc.minesofmystery.custom.CustomItems;
import eu.playsc.minesofmystery.custom.fonts.Caps;
import eu.playsc.minesofmystery.dungeon.DungeonTeam;
import eu.playsc.minesofmystery.dungeon.DungeonTheme;
import eu.playsc.minesofmystery.dungeon.objectives.DungeonObjective;
import eu.playsc.minesofmystery.dungeon.world.DungeonWorld;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DungeonHandler {
	private static DungeonHandler instance;
	private static final Map<String, DungeonWorld> DUNGEONS = new HashMap<>();

	public static final NamespacedKey DUNGEON_DIFFICULTY = new NamespacedKey("minesofmystery", "dungeon_difficulty");
	public static final NamespacedKey DUNGEON_THEME = new NamespacedKey("minesofmystery", "dungeon_theme");
	public static final NamespacedKey DUNGEON_OBJECTIVE = new NamespacedKey("minesofmystery", "dungeon_objective");

	public static DungeonHandler get() {
		if (instance == null) {
			instance = new DungeonHandler();
		}

		return instance;
	}

	public Optional<DungeonWorld> getDungeon(final World world) {
		return DUNGEONS.values().stream()
				.filter(dungeon -> dungeon.getWorld().getName().equals(world.getName()))
				.findFirst();
	}

	public boolean isInDungeon(@NotNull final Player player) {
		return this.getDungeon(player.getWorld()).isPresent();
	}

	public void createDungeon(final Player player, final ItemStack dungeonKey) {
		if (dungeonKey.getItemMeta() == null ||
		    !dungeonKey.getItemMeta().getPersistentDataContainer().has(DUNGEON_DIFFICULTY, PersistentDataType.INTEGER) ||
		    !dungeonKey.getItemMeta().getPersistentDataContainer().has(DUNGEON_THEME, PersistentDataType.STRING) ||
		    !dungeonKey.getItemMeta().getPersistentDataContainer().has(DUNGEON_OBJECTIVE, PersistentDataType.STRING))
			return;

		final int difficulty = dungeonKey.getItemMeta().getPersistentDataContainer().get(DUNGEON_DIFFICULTY, PersistentDataType.INTEGER);
		final DungeonTheme theme = DungeonTheme.get(dungeonKey.getItemMeta().getPersistentDataContainer().get(DUNGEON_THEME, PersistentDataType.STRING));
		final DungeonObjective objective = DungeonObjective.getObjective(NamespacedKey.fromString(dungeonKey.getItemMeta().getPersistentDataContainer().get(DUNGEON_OBJECTIVE, PersistentDataType.STRING)));

		final DungeonWorld world = new DungeonWorld(DungeonTeam.get(player), difficulty, theme, objective);
		DUNGEONS.put(world.getWorld().getName(), world);
	}

	public ItemStack getKey(final int difficulty, final DungeonTheme theme, final DungeonObjective objective) {
		final ItemStack dungeonKey = CustomItems.DUNGEON_KEY.getItemStack();
		final ItemMeta meta = dungeonKey.getItemMeta();

		meta.getPersistentDataContainer().set(DUNGEON_DIFFICULTY, PersistentDataType.INTEGER, difficulty);
		meta.getPersistentDataContainer().set(DUNGEON_THEME, PersistentDataType.STRING, theme.name());
		meta.getPersistentDataContainer().set(DUNGEON_OBJECTIVE, PersistentDataType.STRING, objective.getKey().toString());

		List<Component> lore = List.of(
				Caps.get("Difficulty: ", Color.GRAY).append(Caps.get(difficulty, Color.RED)),
				Component.empty(),
				Caps.get("Theme: ", Color.GRAY).append(theme.getDisplayNameCaps()),
				Caps.get("Objective: ", Color.GRAY).append(objective.getDisplayNameCaps())
		);
		lore = lore.stream().map(component -> component.decoration(TextDecoration.ITALIC, false)).toList();

		meta.lore(lore);
		dungeonKey.setItemMeta(meta);

		return dungeonKey;
	}
}
