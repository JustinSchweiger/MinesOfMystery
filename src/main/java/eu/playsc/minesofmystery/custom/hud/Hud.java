package eu.playsc.minesofmystery.custom.hud;

import eu.playsc.minesofmystery.MinesOfMystery;
import eu.playsc.minesofmystery.common.Common;
import eu.playsc.minesofmystery.custom.internal.CustomFont;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.intellij.lang.annotations.Subst;
import org.json.JSONArray;
import org.json.JSONObject;
import org.reflections.Reflections;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Getter
@NoArgsConstructor(force = true)
public abstract class Hud {
	private final Player player;

	public Hud(final Player player) {
		this.player = player;
	}

	@Subst("default")
	public abstract String getKey();

	public Map<CustomFont, CustomFont.Overwrite> getOverwrites() {
		return Map.of();
	}

	public abstract void render();

	public int getXOffset() {
		return 0;
	}

	public Key getFont() {
		return Key.key("minesofmystery", "hud_" + this.getKey());
	}

	public static void generateFontFiles() {
		final Reflections reflection = new Reflections("eu.playsc.minesofmystery");
		reflection.getSubTypesOf(Hud.class).stream()
				.filter(hudClass -> !hudClass.equals(BossBarHud.class) && !hudClass.equals(ActionBarHud.class))
				.forEach(hudClass -> {
					try {
						final Hud hud = hudClass.getDeclaredConstructor().newInstance();
						if (hud.getOverwrites().isEmpty())
							return;

						final JSONArray providers = new JSONArray();

						// Add space character
						final JSONObject spaceProvider = new JSONObject();
						spaceProvider.put("type", "space");
						spaceProvider.put("advances", new JSONObject().put(" ", CustomFont.SPACE_WIDTH));
						providers.put(spaceProvider);

						for (final Map.Entry<CustomFont, CustomFont.Overwrite> overwrite : hud.getOverwrites().entrySet()) {
							final CustomFont font = overwrite.getKey();
							final CustomFont.Overwrite overwrites = overwrite.getValue();

							final JSONObject provider = new JSONObject();
							provider.put("file", overwrite.getKey().getPath());
							provider.put("chars", new JSONArray().put(font.getCharacter()));

							if (overwrites.hasAscent())
								provider.put("ascent", overwrite.getValue().getAscent());
							else
								provider.put("ascent", font.getAscent());

							if (overwrites.hasHeight())
								provider.put("height", overwrite.getValue().getHeight());
							else
								provider.put("height", font.getHeight());

							provider.put("type", "bitmap");
							providers.put(provider);
						}

						final JSONObject root = new JSONObject();
						root.put("providers", providers);

						final File HUD_FILE = new File(MinesOfMystery.getInstance().getDataFolder(), "resources/assets/minesofmystery/font/hud_" + hud.getKey() + ".json");
						HUD_FILE.getParentFile().mkdirs();

						try (final OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(HUD_FILE), StandardCharsets.UTF_8)) {
							writer.write(Common.escapeUnicode(root.toString(2)));
						} catch (final IOException e) {
							Common.error("Failed to generate HUD font file for: " + hudClass.getName(), e);
						}
					} catch (final Exception e) {
						Common.error("Failed to generate HUD font file for: " + hudClass.getName(), e);
					}
				});
	}
}
