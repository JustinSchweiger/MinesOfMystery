package eu.playsc.minesofmystery.listeners;

import eu.playsc.minesofmystery.annotations.AutoRegister;
import eu.playsc.minesofmystery.common.Color;
import eu.playsc.minesofmystery.common.Message;
import eu.playsc.minesofmystery.common.concurrency.Concurrency;
import eu.playsc.minesofmystery.custom.fonts.Caps;
import eu.playsc.minesofmystery.dungeon.DungeonTheme;
import eu.playsc.minesofmystery.dungeon.handlers.DungeonHandler;
import eu.playsc.minesofmystery.dungeon.objectives.DungeonObjective;
import eu.playsc.minesofmystery.dungeon.world.DungeonWorld;
import eu.playsc.minesofmystery.events.DungeonPlayerLeaveEvent;
import eu.playsc.minesofmystery.events.DungeonTeamJoinEvent;
import eu.playsc.minesofmystery.model.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import java.time.Duration;
import java.util.Optional;

@AutoRegister
public class DungeonListener implements Listener {
	
	@EventHandler
	public void onJoin(final DungeonTeamJoinEvent event) {
		event.getDungeon().startDungeonTick(1200);

		Message.broadcast(Component.text(event.getTeam().getLeader().getUsername() + "'s").color(Color.ORANGE).append(Component.text(" team joined the dungeon!").color(Color.YELLOW)));

		final Title title = Title.title(
				Caps.get("You entered a Dungeon", Color.PURPLE),
				Caps.get("Theme: ", Color.GRAY).append(Caps.get(event.getDungeon().getTheme().getDisplayNameString(), event.getDungeon().getTheme().getColor())),
				Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(5), Duration.ofSeconds(1))
		);

		Concurrency.runLater(60, () -> event.getTeam().getPlayers().forEach(player -> player.showTitle(title)));
	}

	@EventHandler
	public void onLeave(final DungeonPlayerLeaveEvent event) {
		switch (event.getReason()) {
			case PLAYER_LEFT -> {
				event.getPlayer().teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
				Message.broadcast(Component.text(event.getPlayer().getName()).color(Color.ORANGE).append(Component.text(" left the dungeon!").color(Color.GRAY)));
			}
			case OUT_OF_TIME -> {
				Message.broadcast(Component.text(event.getPlayer().getName()).color(Color.ORANGE).append(Component.text(" ran out of time!").color(Color.RED)));
			}
			case DEFEATED -> {
				Message.broadcast(Component.text(event.getPlayer().getName()).color(Color.ORANGE).append(Component.text(" was defeated!").color(Color.RED)));
			}
			case COMPLETED -> {
				final Player player = event.getPlayer();
				final PlayerData playerData = PlayerData.from(player);

				player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());

				if (playerData.getDungeonLevel() < event.getDungeon().getDifficulty() + 1) {
					playerData.setDungeonLevel(event.getDungeon().getDifficulty() + 1);
					playerData.save();
				}

				// Reward the leader with a new key
				if (player.getUniqueId() == event.getTeam().getLeader().getUuid()) {
					player.getInventory().addItem(DungeonHandler.get().getKey(event.getTeam().getLeader().getDungeonLevel(), DungeonTheme.getRandomTheme(), DungeonObjective.getRandomObjective()));
				}
			}
		}
	}

	@EventHandler
	public void onLeave(final PlayerChangedWorldEvent event) {
		if (!event.getFrom().getName().contains("dungeon_"))
			return;

		final Optional<DungeonWorld> dungeon = DungeonHandler.get().getDungeon(event.getFrom());
		if (dungeon.isEmpty())
			return;

		if (dungeon.get().getWorld().getPlayers().isEmpty())
			dungeon.get().deleteDungeon();
	}
}
