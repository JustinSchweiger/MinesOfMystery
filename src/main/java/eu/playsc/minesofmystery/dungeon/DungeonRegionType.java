package eu.playsc.minesofmystery.dungeon;

import eu.playsc.minesofmystery.MinesOfMystery;
import lombok.Getter;

import java.io.File;
import java.util.Arrays;

@Getter
public enum DungeonRegionType {
	ROOM("room"),
	CORRIDOR("corridor"),
	ENTRANCE("entrance"),
	BOSS_ROOM("boss_room");

	private final File folder;

	DungeonRegionType(final String name) {
		final File DUNGEON_REGIONS_FOLDER = new File(MinesOfMystery.getInstance().getDataFolder(), "dungeon");
		this.folder = new File(DUNGEON_REGIONS_FOLDER, name);

		if (!this.folder.exists()) {
			this.folder.mkdirs();
		}
	}

	public static String[] getNames() {
		return Arrays.stream(values()).map(Enum::name).toArray(String[]::new);
	}

	public static DungeonRegionType get(final String type) {
		try {
			return DungeonRegionType.valueOf(type.toUpperCase());
		} catch (final IllegalArgumentException e) {
			return ROOM;
		}
	}
}
