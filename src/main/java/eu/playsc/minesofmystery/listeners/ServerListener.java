package eu.playsc.minesofmystery.listeners;

import eu.playsc.minesofmystery.MinesOfMystery;
import eu.playsc.minesofmystery.annotations.AutoRegister;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

@AutoRegister
public class ServerListener implements Listener {
	@EventHandler
	public void onServerLoad(final ServerLoadEvent event) {
		MinesOfMystery.getResourcepackManager().onServerLoaded();
	}
}
