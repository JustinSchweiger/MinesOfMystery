package eu.playsc.minesofmystery.listeners;

import eu.playsc.minesofmystery.annotations.AutoRegister;
import eu.playsc.minesofmystery.resourcepack.ResourcepackManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@AutoRegister
public class JoinListener implements Listener {

	@EventHandler
	public void onJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		ResourcepackManager.serve(player);
	}
}
