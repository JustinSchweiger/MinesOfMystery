package eu.playsc.minesofmystery;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import eu.playsc.minesofmystery.common.commands.SimpleCommand;
import eu.playsc.minesofmystery.common.custom.CustomBlocks;
import eu.playsc.minesofmystery.common.custom.manager.CustomBlockManager;
import eu.playsc.minesofmystery.common.listeners.AutoRegistering;
import eu.playsc.minesofmystery.resourcepack.ResourcepackManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class MinesOfMystery extends JavaPlugin {
	@Getter
	private static MinesOfMystery instance;
	@Getter
	private static ResourcepackManager resourcepackManager;

	@Override
	public void onLoad() {
		instance = this;
		resourcepackManager = new ResourcepackManager();

		CommandAPI.onLoad(
				new CommandAPIBukkitConfig(this)
						.verboseOutput(false)
						.shouldHookPaperReload(true)
		);
	}

	@Override
	public void onEnable() {
		CommandAPI.onEnable();

		CustomBlockManager.get().init();
		new CustomBlocks();

		AutoRegistering.findAndRegister();
		SimpleCommand.registerCommands();
	}

	@Override
	public void onDisable() {
		resourcepackManager.stopServer();
	}
}
