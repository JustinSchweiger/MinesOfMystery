package eu.playsc.minesofmystery.dungeon.objectives;

import eu.playsc.minesofmystery.custom.hud.BossBarHud;
import eu.playsc.minesofmystery.dungeon.DungeonTeam;
import eu.playsc.minesofmystery.dungeon.world.DungeonWorld;
import eu.playsc.minesofmystery.events.DungeonObjectiveCompletedEvent;
import eu.playsc.minesofmystery.model.PlayerData;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
public abstract class DungeonObjective implements Listener {
	private static final Map<NamespacedKey, DungeonObjective> DUNGEON_OBJECTIVES = new HashMap<>();

	private int difficulty;
	private DungeonWorld dungeonWorld;
	private DungeonTeam dungeonTeam;

	public DungeonObjective() {
		DUNGEON_OBJECTIVES.put(this.getKey(), this);
	}

	public abstract void init();

	public void afterComplete() {
		this.dungeonTeam = null;
		this.dungeonWorld = null;
		this.difficulty = 0;
	}

	public abstract NamespacedKey getKey();

	public abstract BossBarHud getObjectiveHud(Player player);

	public abstract BossBarHud getProgressHud(Player player);

	public abstract Component getDisplayName();

	public abstract Component getDisplayNameCaps();

	public void addHuds(final List<Player> players) {
		players.forEach(player -> PlayerData.from(player).registerObjectiveHuds(this.getObjectiveHud(player), this.getProgressHud(player)));
	}

	public void removeHuds(final List<Player> players) {
		players.forEach(player -> PlayerData.from(player).unregisterObjectiveHuds());
	}

	public void callCompletion() {
		final DungeonObjectiveCompletedEvent event = new DungeonObjectiveCompletedEvent(this);
		event.callEvent();
		HandlerList.unregisterAll(this);
	}

	public static DungeonObjective getObjective(final NamespacedKey objective) {
		return DUNGEON_OBJECTIVES.get(objective);
	}

	@NotNull
	public static DungeonObjective getRandomObjective() {
		return DUNGEON_OBJECTIVES.values().stream().findAny().get();
	}

	public static String[] getNames() {
		return DUNGEON_OBJECTIVES.keySet().stream().map(NamespacedKey::getKey).toArray(String[]::new);
	}

	public static void registerObjectives() {
		final Reflections reflections = new Reflections("eu.playsc.minesofmystery.dungeon.objectives");
		reflections.getSubTypesOf(DungeonObjective.class).forEach(objective -> {
			try {
				objective.getDeclaredConstructor().newInstance();
			} catch (final InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		});
	}
}
