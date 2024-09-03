package eu.playsc.minesofmystery.events;

import eu.playsc.minesofmystery.dungeon.objectives.DungeonObjective;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
@AllArgsConstructor
public class DungeonObjectiveCompletedEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private DungeonObjective objective;

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
