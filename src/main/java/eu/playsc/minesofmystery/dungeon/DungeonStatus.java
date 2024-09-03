package eu.playsc.minesofmystery.dungeon;

import eu.playsc.minesofmystery.custom.fonts.Icons;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;

@Getter
@AllArgsConstructor
public enum DungeonStatus {
	NO_DUNGEON(Icons.NO_DUNGEON.getWithSpace()),
	DUNGEON_GENERATING(Icons.DUNGEON_GENERATING.getWithSpace()),
	DUNGEON_OPEN(Icons.DUNGEON.getWithSpace()),
	DUNGEON_COMPLETED(Icons.DUNGEON_COMPLETED.getWithSpace());

	private final Component icon;
}
