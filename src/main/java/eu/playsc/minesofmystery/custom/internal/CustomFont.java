package eu.playsc.minesofmystery.custom.internal;

import eu.playsc.minesofmystery.MinesOfMystery;
import eu.playsc.minesofmystery.common.Color;
import eu.playsc.minesofmystery.common.Common;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.reflections.Reflections;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor(force = true)
public abstract class CustomFont {
	private static final File FONT_DIRECTORY;

	static {
		FONT_DIRECTORY = new File(MinesOfMystery.getInstance().getDataFolder(), "/resources/assets/minesofmystery/font/");
	}

	static final Map<String, List<CustomFont>> FONTS = new HashMap<>();
	public static final int SPACE_WIDTH = 4;

	private final String fontKey;
	private final String character;
	private final String path;
	private final int height;
	private final int ascent;
	private final int imageWidth;
	private final int imageHeight;

	public CustomFont(final String fontKey, final String character, final String path, final int height, final int ascent, final int imageWidth, final int imageHeight) {
		this.fontKey = fontKey;
		this.character = character;
		this.path = path;
		this.height = height;
		this.ascent = ascent;
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;

		if (!FONTS.containsKey(this.fontKey))
			FONTS.put(this.fontKey, new ArrayList<>());

		FONTS.get(this.fontKey).add(this);
	}

	public Component get() {
		return this.get(Color.WHITE);
	}

	public Component get(final TextColor color) {
		return Component.text(this.getCharacter()).color(color).font(this.getFontKey());
	}

	public Component getWithSpace() {
		return this.getWithSpace(Color.WHITE);
	}

	public Component getWithSpace(final TextColor color) {
		return Component.text(this.getCharacter() + " ").color(color).font(this.getFontKey());
	}

	public Key getFontKey() {
		return Key.key("minesofmystery", this.fontKey);
	}

	public static CountResult countCharacters(final String string) {
		final Map<CustomFont, Integer> characters = new HashMap<>();
		int spaceCount = 0;

		for (final char character : string.toCharArray()) {
			if (character == ' ') {
				spaceCount++;
				continue;
			}

			final CustomFont font = FONTS.values().stream().flatMap(List::stream).filter(f -> f.getCharacter().equals(String.valueOf(character))).findFirst().orElse(null);
			if (font == null)
				continue;

			characters.put(font, characters.getOrDefault(font, 0) + 1);
		}

		return new CountResult(spaceCount, characters);
	}

	public static CountResult countCharacters(final Component component) {
		final String string = ((TextComponent) component).content() +
		                      component.children().stream()
				                      .map(c -> {
					                      if (c instanceof TextComponent)
						                      return ((TextComponent) c).content();

					                      return "";
				                      })
				                      .reduce("", String::concat);

		return countCharacters(string);
	}

	public static void generateFontFiles() {
		final Reflections reflection = new Reflections("eu.playsc.minesofmystery.custom.fonts");
		reflection.getSubTypesOf(CustomFont.class).forEach(font -> {
			try {
				font.getDeclaredConstructor().newInstance();
			} catch (final Exception e) {
				Common.error("Failed to generate font file for: minesofmystery:" + font.getSimpleName(), e);
			}
		});

		for (final Map.Entry<String, List<CustomFont>> entry : FONTS.entrySet()) {
			final JSONArray providers = new JSONArray();

			// Add space character
			final JSONObject spaceProvider = new JSONObject();
			spaceProvider.put("type", "space");
			spaceProvider.put("advances", new JSONObject().put(" ", SPACE_WIDTH));
			providers.put(spaceProvider);

			final String fontKey = entry.getKey();
			final List<CustomFont> fonts = entry.getValue();

			for (final CustomFont font : fonts) {
				final JSONObject provider = new JSONObject();
				provider.put("file", font.getPath());
				provider.put("chars", new JSONArray().put(font.character));
				provider.put("height", font.getHeight());
				provider.put("ascent", font.getAscent());
				provider.put("type", "bitmap");
				providers.put(provider);
			}

			final JSONObject root = new JSONObject();
			root.put("providers", providers);

			try (final OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(FONT_DIRECTORY + "/" + fontKey + ".json"), StandardCharsets.UTF_8)) {
				writer.write(Common.escapeUnicode(root.toString(2)));
			} catch (final IOException e) {
				Common.error("Failed to generate font file for: stonecraft:" + fontKey, e);
			}
		}
	}

	@Getter
	public static class CountResult {
		private final int spaceCount;
		private final Map<CustomFont, Integer> characters;

		public CountResult(final int spaceCount, final Map<CustomFont, Integer> characters) {
			this.spaceCount = spaceCount;
			this.characters = characters;
		}

		public int calculateWidth(final Map<CustomFont, CustomFont.Overwrite> overwrites) {
			int width = 0;

			for (final Map.Entry<CustomFont, Integer> entry : this.characters.entrySet()) {
				final CustomFont font = entry.getKey();
				final int count = entry.getValue();

				final CustomFont.Overwrite overwrite = overwrites.get(font);
				if (overwrite == null) {
					// No Overwrite? Use the default width
					width += font.getImageWidth() * count;
				} else {
					if (overwrite.hasHeight())
						// ToDo: Scale down image width to match the new height
						width += overwrite.getHeight() * count;
					else {
						if (font.getImageHeight() > font.getHeight())
							width += font.getHeight() * count;
						else
							width += font.getImageWidth() * count;
					}
				}
			}

			// Add full space width
			width += this.spaceCount * SPACE_WIDTH;

			// Add another pixel for each character to account for the space between characters
			width += this.characters.values().stream().mapToInt(i -> i).sum() + this.spaceCount;

			return width;
		}

		@Override
		public String toString() {
			return "CountResult{" +
			       "spaceCount=" + this.spaceCount +
			       ", characters=" + this.characters +
			       '}';
		}
	}

	@Getter
	public static final class Overwrite {
		private final int height;
		private final long ascent;

		public Overwrite(final int height, final long ascent) {
			this.height = height;
			this.ascent = ascent;
		}

		public Overwrite(final int height) {
			this.height = height;
			this.ascent = -999999;
		}

		public Overwrite(final long ascent) {
			this.height = -999999;
			this.ascent = ascent;
		}

		public boolean hasHeight() {
			return this.height != -999999;
		}

		public boolean hasAscent() {
			return this.ascent != -999999;
		}
	}
}
