package eu.playsc.minesofmystery.listeners;

import eu.playsc.minesofmystery.annotations.AutoRegister;
import eu.playsc.minesofmystery.custom.resourcepack.ResourcepackManager;
import eu.playsc.minesofmystery.dungeon.DungeonTeam;
import eu.playsc.minesofmystery.model.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@AutoRegister
public class ConnectionListener implements Listener {

	@EventHandler
	public void onJoin(final PlayerJoinEvent event) {
		PlayerData.createOrUpdate(event.getPlayer());
		ResourcepackManager.serve(event.getPlayer());
		DungeonTeam.get(event.getPlayer());
	}

	@EventHandler
	public void onLeave(final PlayerQuitEvent event) {
		PlayerData.remove(event.getPlayer());
	}
}

