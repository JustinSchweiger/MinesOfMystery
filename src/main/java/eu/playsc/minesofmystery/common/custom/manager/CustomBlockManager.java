package eu.playsc.minesofmystery.common.custom.manager;

import de.tr7zw.changeme.nbtapi.NBTFile;
import eu.playsc.minesofmystery.MinesOfMystery;
import eu.playsc.minesofmystery.common.custom.CustomBlock;
import org.bukkit.NamespacedKey;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CustomBlockManager {
	private static int customModelDataStart = 1000000;

	private static CustomBlockManager instance;

	private boolean initComplete = false;
	private final Map<NamespacedKey, CustomBlock> customBlocks = new HashMap<>();

	private final File STORAGE_FILE = new File(MinesOfMystery.getInstance().getDataFolder(), "storage/custom_blocks.nbt");
	private final File MODEL_FILE = new File(MinesOfMystery.getInstance().getDataFolder(), "resources/assets/minecraft/models/item/stone.json");

	public static CustomBlockManager get() {
		if (instance == null) {
			instance = new CustomBlockManager();
		}

		return instance;
	}

	public void init() {
		if (this.initComplete) {
			throw new IllegalStateException("CustomBlockManager has already been initialized!");
		}

		if (!this.STORAGE_FILE.exists()) {
			try {
				this.STORAGE_FILE.createNewFile();
			} catch (final Exception e) {
				throw new RuntimeException("Failed to create storage file for custom blocks!", e);
			}
		} else {
			this.STORAGE_FILE.delete();
		}

		if (!this.MODEL_FILE.exists()) {
			try {
				this.MODEL_FILE.createNewFile();
			} catch (final Exception e) {
				throw new RuntimeException("Failed to create model file for custom blocks!", e);
			}
		} else {
			this.MODEL_FILE.delete();
		}

		this.initComplete = true;
	}

	private boolean checkInit() {
		if (!this.initComplete) {
			throw new IllegalStateException("CustomBlockManager has not been initialized yet! Please call its init() method before registering any custom blocks.");
		}

		return true;
	}

	public void registerCustomBlock(final CustomBlock block) {
		this.checkInit();

		this.customBlocks.put(block.getKey(), block);

		this.persistModelData(block);
		this.generateModelOverwrite(block);
	}

	private void generateModelOverwrite(final CustomBlock block) {
		// Check if the file exists and if so read its content, then add the overwrite for the block
		JSONObject json = new JSONObject();
		if (this.MODEL_FILE.exists()) {
			try {
				final String content = Files.readString(this.MODEL_FILE.toPath());
				json = new JSONObject(content);
			} catch (final Exception e) {
				throw new RuntimeException("Failed to read model file for custom blocks!", e);
			}
		} else {
			try {
				this.MODEL_FILE.createNewFile();
				json = new JSONObject();
				json.put("parent", "minecraft:block/stone");
				json.put("overrides", new JSONArray());
			} catch (final Exception e) {
				throw new RuntimeException("Failed to create model file for custom blocks!", e);
			}
		}

		final JSONObject override = new JSONObject();
		override.put("predicate", new JSONObject().put("custom_model_data", block.getCustomModelData()));
		override.put("model", block.getModel());

		json.getJSONArray("overrides").put(override);

		try {
			Files.writeString(this.MODEL_FILE.toPath(), json.toString());
		} catch (final Exception e) {
			throw new RuntimeException("Failed to write model file for custom blocks!", e);
		}
	}

	public List<String> getRegisteredBlocks() {
		return List.copyOf(this.customBlocks.keySet().stream().map(NamespacedKey::asString).toList());
	}

	private int getNextCustomModelData() {
		return customModelDataStart++;
	}

	private void persistModelData(final CustomBlock block) {
		try {
			final NBTFile file = new NBTFile(this.STORAGE_FILE);
			if (file.hasTag(block.getKey().asString()))
				return;

			final int customModelData = this.getNextCustomModelData();

			block.setCustomModelData(customModelData);
			file.setInteger(block.getKey().asString(), customModelData);
			file.save();
		} catch (final Exception e) {
			throw new RuntimeException("Failed to persist custom block data!", e);
		}
	}

	public Optional<CustomBlock> getCustomBlock(final NamespacedKey key) {
		this.checkInit();

		return Optional.ofNullable(this.customBlocks.get(key));
	}
}
