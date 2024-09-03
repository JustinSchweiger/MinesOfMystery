package eu.playsc.minesofmystery.dungeon.loot;

import eu.playsc.minesofmystery.dungeon.DungeonTeam;
import eu.playsc.minesofmystery.dungeon.DungeonTheme;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

@Getter
public class DungeonLootContext {
	private final Location location;
	@Nullable private final Entity lootedEntity;
	private final Player player;
	private final int dungeonDifficulty;
	private final DungeonTheme dungeonTheme;

	public DungeonLootContext(final Location location, final @Nullable Entity lootedEntity, final Player player) {
		this.location = location;
		this.lootedEntity = lootedEntity;
		this.player = player;

		final DungeonTeam team = DungeonTeam.get(player);
		this.dungeonDifficulty = team.getDungeon().getDifficulty();
		this.dungeonTheme = team.getDungeon().getTheme();
	}
}
