package eu.playsc.minesofmystery.dungeon;

import eu.playsc.minesofmystery.common.Color;
import eu.playsc.minesofmystery.custom.fonts.Caps;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.Random;

@Getter
public enum DungeonTheme {
	DRIPSTONE_CAVES(
			"dripstone_caves",
			"Dripstone Caves",
			Color.DRIPSTONE_CAVES
	);

	private final String name;
	private final String displayNameString;
	private final TextColor color;

	DungeonTheme(final String name, final String displayNameString, final TextColor color) {
		this.name = name;
		this.displayNameString = displayNameString;
		this.color = color;

		for (final DungeonRegionType regionType : DungeonRegionType.values()) {
			final File folder = new File(regionType.getFolder(), name);
			if (!folder.exists()) {
				folder.mkdirs();
			}
		}
	}

	public Component getDisplayName() {
		return Component.text(this.displayNameString).color(this.color);
	}

	public Component getDisplayNameCaps() {
		return Caps.get(this.displayNameString, this.color);
	}

	public File getFolder(final DungeonRegionType type) {
		return new File(type.getFolder(), this.name);
	}

	public static String[] getNames() {
		return Arrays.stream(values()).map(Enum::name).toArray(String[]::new);
	}

	public static DungeonTheme getRandomTheme() {
		return values()[new Random().nextInt(values().length)];
	}

	@NotNull
	public static DungeonTheme get(final String type) {
		try {
			return DungeonTheme.valueOf(type.toUpperCase());
		} catch (final IllegalArgumentException e) {
			return DungeonTheme.getRandomTheme();
		}
	}
}
