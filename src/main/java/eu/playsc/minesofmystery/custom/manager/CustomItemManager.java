package eu.playsc.minesofmystery.custom.manager;

import eu.playsc.minesofmystery.MinesOfMystery;
import eu.playsc.minesofmystery.common.Common;
import eu.playsc.minesofmystery.custom.internal.CustomItem;
import lombok.SneakyThrows;
import org.bukkit.Material;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomItemManager extends CustomManager {
	private static CustomItemManager instance;

	public static CustomItemManager get() {
		if (instance == null) {
			instance = new CustomItemManager();
		}

		return instance;
	}

	private CustomItemManager() {
		super();
	}

	@SneakyThrows
	@Override
	public void generateModelFiles() {
		Common.log("Generating model files for custom items...");

		final Map<Material, List<CustomItem>> mapper = new HashMap<>();
		for (final CustomItem item : this.getCustomItems().values()) {
			if (!mapper.containsKey(item.getMaterial())) {
				mapper.put(item.getMaterial(), new ArrayList<>());
				mapper.get(item.getMaterial()).add(item);
			} else {
				mapper.get(item.getMaterial()).add(item);
			}
		}

		for (final Map.Entry<Material, List<CustomItem>> entry : mapper.entrySet()) {
			final File MODEL_FILE = new File(MinesOfMystery.getInstance().getDataFolder(), "resources/assets/minecraft/models/item/" + entry.getKey().getKey().getKey() + ".json");
			MODEL_FILE.getParentFile().mkdirs();

			if (MODEL_FILE.exists())
				MODEL_FILE.delete();

			final JSONObject json = new JSONObject();
			json.put("parent", "minecraft:item/generated");

			if (entry.getValue().stream().anyMatch(item -> item.getCustomTextures().isHasTextures()))
				json.put("textures", entry.getValue().get(0).getCustomTextures().getTextures());
			else
				json.put("textures", new JSONObject().put("layer0", "minecraft:item/" + entry.getKey().getKey().getKey()));

			json.put("overrides", new JSONArray());

			for (final CustomItem item : entry.getValue()) {
				final JSONObject override = new JSONObject();
				override.put("predicate", new JSONObject().put("custom_model_data", item.getCustomModelData()));
				override.put("model", item.getModel());

				json.getJSONArray("overrides").put(override);
			}

			try {
				MODEL_FILE.createNewFile();
				Files.writeString(MODEL_FILE.toPath(), json.toString());
			} catch (final Exception e) {
				throw new RuntimeException("Failed to write model file for custom item!", e);
			}
		}
	}
}
