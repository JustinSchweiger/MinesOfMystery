package eu.playsc.minesofmystery.dungeon.objectives;

import eu.playsc.minesofmystery.common.Color;
import eu.playsc.minesofmystery.custom.fonts.Caps;
import eu.playsc.minesofmystery.custom.hud.BossBarHud;
import eu.playsc.minesofmystery.custom.internal.CustomFont;
import eu.playsc.minesofmystery.dungeon.DungeonStatus;
import eu.playsc.minesofmystery.dungeon.DungeonTeam;
import eu.playsc.minesofmystery.dungeon.entity.DungeonEntity;
import eu.playsc.minesofmystery.dungeon.world.DungeonWorld;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;

public class KillMonstersObjective extends DungeonObjective {
	private static final NamespacedKey OBJECTIVE = new NamespacedKey("minesofmystery", "kill_monsters_objective");
	private static final int BASE_MONSTERS_REQUIRED = 20;
	private static final double BASE_MONSTERS_DIFFICULTY_SCALING = 1.1;

	private long dungeonEntityCount;
	private long requiredDungeonEntityCount;
	@Getter
	private boolean bossSpawnable = false;
	@Setter
	private boolean bossSpawned = false;
	@Setter
	@Getter
	private boolean bossRoomPlaced = false;


	@Override
	public void init() {
		this.dungeonEntityCount = 0;
		final int memberCount = this.getDungeonTeam().getMembers().size();

		this.requiredDungeonEntityCount = (long) (BASE_MONSTERS_REQUIRED * Math.pow(BASE_MONSTERS_DIFFICULTY_SCALING, this.getDifficulty()) * memberCount);
	}

	@Override
	public NamespacedKey getKey() {
		return OBJECTIVE;
	}

	@Override
	public BossBarHud getObjectiveHud(final Player player) {
		return new ObjectiveHud(player);
	}

	@Override
	public BossBarHud getProgressHud(final Player player) {
		return new ObjectiveValueHud(player);
	}

	@Override
	public Component getDisplayName() {
		return Component.text("Monster Slayer").color(Color.KILL_MONSTERS_OBJECTIVE);
	}

	@Override
	public Component getDisplayNameCaps() {
		return Caps.get("Monster Slayer", Color.KILL_MONSTERS_OBJECTIVE);
	}

	@EventHandler
	public void onKill(final EntityDeathEvent event) {
		final LivingEntity entity = event.getEntity();

		if (!entity.getPersistentDataContainer().has(DungeonEntity.DUNGEON_ENTITY, PersistentDataType.BOOLEAN))
			return;

		if (!this.bossSpawnable) {
			this.dungeonEntityCount++;

			if (this.dungeonEntityCount >= this.requiredDungeonEntityCount) {
				this.bossSpawnable = true;
			}
		}

		if (!entity.getPersistentDataContainer().has(DungeonEntity.DUNGEON_BOSS_ENTITY, PersistentDataType.BOOLEAN))
			return;

		this.callCompletion();
	}

	@NoArgsConstructor(force = true)
	public static class ObjectiveHud extends BossBarHud {
		public ObjectiveHud(final Player player) {
			super(player);
		}

		@Override
		public String getKey() {
			return OBJECTIVE.getKey();
		}

		@Override
		public Map<CustomFont, CustomFont.Overwrite> getOverwrites() {
			final long Y_OFFSET = -4;

			return new HashMap<>() {{
				this.putAll(Caps.getOverwrite(Caps.Type.DIGIT, Y_OFFSET));
				this.putAll(Caps.getOverwrite(Caps.Type.LETTER, Y_OFFSET));
				this.putAll(Caps.getOverwrite(Caps.Type.SYMBOL, Y_OFFSET));
			}};
		}

		@Override
		public void render() {
			final Player player = this.getPlayer();
			final DungeonTeam team = DungeonTeam.get(player);
			final DungeonWorld dungeon = team.getDungeon();
			final KillMonstersObjective objective = (KillMonstersObjective) dungeon.getObjective();

			if (team.getStatus() != DungeonStatus.DUNGEON_OPEN) {
				this.hide();
				return;
			}

			this.show();

			final Component component;
			if (!objective.bossSpawnable) {
				// Need to slay more monsters
				component = Caps.get("slay dungeon monsters!", Color.WHITE).font(this.getFont());
			} else if (!objective.bossSpawned) {
				// Find the boss room
				component = Caps.get("find a boss room!", Color.WHITE).font(this.getFont());
			} else {
				// Defeat the boss
				component = Caps.get("defeat the boss!", Color.WHITE).font(this.getFont());
			}

			this.update(component);
		}
	}

	@NoArgsConstructor(force = true)
	public static class ObjectiveValueHud extends BossBarHud {
		public ObjectiveValueHud(final Player player) {
			super(player);
		}

		@Override
		public String getKey() {
			return OBJECTIVE.getKey() + "_value";
		}

		@Override
		public void render() {
			final Player player = this.getPlayer();
			final DungeonTeam team = DungeonTeam.get(player);
			final DungeonWorld dungeon = team.getDungeon();
			final KillMonstersObjective objective = (KillMonstersObjective) dungeon.getObjective();

			if (team.getStatus() != DungeonStatus.DUNGEON_OPEN || objective.bossSpawnable) {
				this.hide();
				return;
			}

			this.show();
			final Component component = Caps.get(objective.requiredDungeonEntityCount - objective.dungeonEntityCount + " left", Color.GRAY);
			this.update(component);
		}
	}
}
