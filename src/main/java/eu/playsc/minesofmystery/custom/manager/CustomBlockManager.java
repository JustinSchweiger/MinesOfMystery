package eu.playsc.minesofmystery.custom.manager;

import eu.playsc.minesofmystery.MinesOfMystery;
import eu.playsc.minesofmystery.custom.internal.CustomBlock;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Files;

public class CustomBlockManager extends CustomManager {
	private static CustomBlockManager instance;
	private static final File MODEL_FILE = new File(MinesOfMystery.getInstance().getDataFolder(), "resources/assets/minecraft/models/item/stone.json");

	public static CustomBlockManager get() {
		if (instance == null) {
			instance = new CustomBlockManager();
		}

		return instance;
	}

	private CustomBlockManager() {
		super();
	}

	@Override
	public void generateModelFiles() {
		MODEL_FILE.getParentFile().mkdirs();

		if (MODEL_FILE.exists())
			MODEL_FILE.delete();

		final JSONObject json = new JSONObject();
		json.put("parent", "minecraft:block/stone");
		json.put("overrides", new JSONArray());

		for (final CustomBlock block : this.getCustomBlocks().values()) {
			final JSONObject override = new JSONObject();
			override.put("predicate", new JSONObject().put("custom_model_data", block.getCustomModelData()));
			override.put("model", block.getModel());
			json.getJSONArray("overrides").put(override);
		}

		try {
			MODEL_FILE.createNewFile();
			Files.writeString(MODEL_FILE.toPath(), json.toString());
		} catch (final Exception e) {
			throw new RuntimeException("Failed to write model file for custom blocks!", e);
		}
	}
}
