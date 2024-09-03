package eu.playsc.minesofmystery;

import com.github.fierioziy.particlenativeapi.api.ParticleNativeAPI;
import com.github.fierioziy.particlenativeapi.core.ParticleNativeCore;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import eu.playsc.minesofmystery.common.commands.SimpleCommand;
import eu.playsc.minesofmystery.common.listeners.AutoRegistering;
import eu.playsc.minesofmystery.custom.CustomBlocks;
import eu.playsc.minesofmystery.custom.CustomItems;
import eu.playsc.minesofmystery.custom.CustomSounds;
import eu.playsc.minesofmystery.custom.internal.CustomFont;
import eu.playsc.minesofmystery.custom.resourcepack.ResourcepackManager;
import eu.playsc.minesofmystery.database.Database;
import eu.playsc.minesofmystery.dungeon.objectives.DungeonObjective;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;


public final class MinesOfMystery extends JavaPlugin {
	public static final String DATABASE_NAME = "minesofmystery";

	@Getter
	private static MinesOfMystery instance;
	@Getter
	private static ResourcepackManager resourcepackManager;
	@Getter
	private static ParticleNativeAPI particleApi;

	@Override
	public void onLoad() {
		instance = this;
		resourcepackManager = new ResourcepackManager();

		// Init Database
		new Database();

		// Init custom blocks
		new CustomBlocks();

		// Init custom items
		new CustomItems();

		// Init custom sounds
		CustomSounds.generateSounds();

		// Init custom fonts
		CustomFont.generateFontFiles();

		// Register Dungeon Objectives
		DungeonObjective.registerObjectives();

		CommandAPI.onLoad(
				new CommandAPIBukkitConfig(this)
						.verboseOutput(false)
						.shouldHookPaperReload(true)
		);

		particleApi = ParticleNativeCore.loadAPI(this);
	}

	@Override
	public void onEnable() {
		CommandAPI.onEnable();

		// Register listeners and commands
		AutoRegistering.findAndRegister();
		SimpleCommand.registerCommands();
	}

	@Override
	public void onDisable() {
		resourcepackManager.stopServer();
	}
}
