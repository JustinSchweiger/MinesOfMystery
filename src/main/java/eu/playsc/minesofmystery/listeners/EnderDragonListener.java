package eu.playsc.minesofmystery.listeners;

import eu.playsc.minesofmystery.annotations.AutoRegister;
import eu.playsc.minesofmystery.common.Color;
import eu.playsc.minesofmystery.custom.fonts.Caps;
import eu.playsc.minesofmystery.dungeon.DungeonTheme;
import eu.playsc.minesofmystery.dungeon.handlers.DungeonHandler;
import eu.playsc.minesofmystery.dungeon.objectives.DungeonObjective;
import eu.playsc.minesofmystery.model.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.time.Duration;
import java.util.Collection;

@AutoRegister
public class EnderDragonListener implements Listener {
	@EventHandler
	public void onDragonDeath(final EntityDeathEvent event) {
		if (event.getEntity().getType() != EntityType.ENDER_DRAGON)
			return;

		final Collection<Player> playersInEnd = event.getEntity().getTrackedBy();

		final Title title = Title.title(
				Component.text("Congratulations!").color(Color.GREEN),
				Caps.get("You may now enter the dungeon!").color(Color.GRAY),
				Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(5), Duration.ofSeconds(1))
		);

		for (final Player player : playersInEnd) {
			final PlayerData playerData = PlayerData.from(player);
			playerData.setEnderDragonDefeated(true);
			playerData.save();

			player.showTitle(title);
			player.getInventory().addItem(DungeonHandler.get().getKey(1, DungeonTheme.getRandomTheme(), DungeonObjective.getRandomObjective()));
		}
	}
}
