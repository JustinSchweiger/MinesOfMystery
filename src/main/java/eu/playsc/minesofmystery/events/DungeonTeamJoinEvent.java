package eu.playsc.minesofmystery.events;

import eu.playsc.minesofmystery.dungeon.DungeonTeam;
import eu.playsc.minesofmystery.dungeon.world.DungeonWorld;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
@AllArgsConstructor
public class DungeonTeamJoinEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private final DungeonTeam team;
	private final DungeonWorld dungeon;
	
	@Override
	public @NotNull HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
