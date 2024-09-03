package eu.playsc.minesofmystery.custom;

import eu.playsc.minesofmystery.MinesOfMystery;
import lombok.Getter;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Files;
import java.util.Collection;

@Getter
public enum CustomSounds {
	WAVE("minesofmystery:dungeon.wave"),
	DASH("minesofmystery:spells.dash"),
	ERROR("minesofmystery:spells.error"),
	FIREBALL_SHOOT("minesofmystery:spells.fireball_shoot"),
	FIREBALL_IMPACT("minesofmystery:spells.fireball_impact");

	private final String path;

	CustomSounds(final String path) {
		this.path = path;
	}

	public void play(final Location location) {
		location.getWorld().playSound(location, this.path, 1, 1);
	}

	public void play(final Location location, final float volume, final float pitch) {
		location.getWorld().playSound(location, this.path, volume, pitch);
	}

	public void play(final Collection<Player> players, Location location, float volume, float pitch) {
		for (final Player player : players) {
			player.playSound(location, this.path, volume, pitch);
		}
	}

	public void play(final Player player) {
		player.playSound(player.getLocation(), this.path, 1, 1);
	}

	public void play(final Player player, final float volume, final float pitch) {
		player.playSound(player.getLocation(), this.path, volume, pitch);
	}

	public static void generateSounds() {
		final File file = new File(MinesOfMystery.getInstance().getDataFolder(), "resources/assets/minesofmystery/sounds.json");
		if (!file.exists())
			file.getParentFile().mkdirs();
		else
			file.delete();

		final JSONObject sounds = new JSONObject();
		for (final CustomSounds sound : CustomSounds.values()) {
			final JSONObject soundObject = new JSONObject();
			soundObject.put("sounds", new JSONArray().put(sound.path.replace(".", "/")));

			sounds.put(sound.path.replace("minesofmystery:", ""), soundObject);
		}

		try {
			file.createNewFile();
			Files.writeString(file.toPath(), sounds.toString());
		} catch (final Exception e) {
			throw new RuntimeException("Failed to write custom sounds file!", e);
		}
	}

	public @NotNull Key key() {
		return null;
	}
}
