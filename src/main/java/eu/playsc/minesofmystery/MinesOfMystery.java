package eu.playsc.minesofmystery;

import eu.playsc.minesofmystery.common.AutoRegistering;
import eu.playsc.minesofmystery.resourcepack.ResourcepackManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class MinesOfMystery extends JavaPlugin {

	@Getter
	private static MinesOfMystery instance;
	@Getter
	private static ResourcepackManager resourcepackManager;

	@Override
	public void onEnable() {
		instance = this;
		resourcepackManager = new ResourcepackManager();

		AutoRegistering.findAndRegister();
	}

	@Override
	public void onDisable() {
		resourcepackManager.stopServer();
	}
}
