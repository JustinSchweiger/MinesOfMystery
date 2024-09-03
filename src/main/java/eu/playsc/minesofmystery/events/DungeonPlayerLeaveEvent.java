package eu.playsc.minesofmystery.events;

import eu.playsc.minesofmystery.dungeon.DungeonTeam;
import eu.playsc.minesofmystery.dungeon.world.DungeonWorld;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
@AllArgsConstructor
public class DungeonPlayerLeaveEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private final Player player;
	private final DungeonWorld dungeon;
	private final DungeonTeam team;
	private final long timeLeft;
	private final Reason reason;

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public enum Reason {
		OUT_OF_TIME,
		PLAYER_LEFT,
		DEFEATED,
		COMPLETED
	}
}
