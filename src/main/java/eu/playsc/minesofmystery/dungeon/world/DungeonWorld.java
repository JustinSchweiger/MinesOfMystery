package eu.playsc.minesofmystery.dungeon.world;

import eu.playsc.minesofmystery.MinesOfMystery;
import eu.playsc.minesofmystery.common.Color;
import eu.playsc.minesofmystery.common.Common;
import eu.playsc.minesofmystery.common.LogLevel;
import eu.playsc.minesofmystery.common.Message;
import eu.playsc.minesofmystery.common.concurrency.Concurrency;
import eu.playsc.minesofmystery.common.concurrency.SimpleTask;
import eu.playsc.minesofmystery.custom.CustomBlocks;
import eu.playsc.minesofmystery.custom.fonts.Caps;
import eu.playsc.minesofmystery.custom.internal.CustomBlock;
import eu.playsc.minesofmystery.dungeon.DungeonStatus;
import eu.playsc.minesofmystery.dungeon.DungeonTeam;
import eu.playsc.minesofmystery.dungeon.DungeonTheme;
import eu.playsc.minesofmystery.dungeon.objectives.DungeonObjective;
import eu.playsc.minesofmystery.events.DungeonObjectiveCompletedEvent;
import eu.playsc.minesofmystery.events.DungeonPlayerLeaveEvent;
import eu.playsc.minesofmystery.events.DungeonTeamJoinEvent;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class DungeonWorld implements Listener {
	private static final File WORLDS_DIRECTORY = new File(Bukkit.getServer().getWorldContainer().getPath(), "/dungeons");
	private static final int GENERATION_TIME = 5;
	private final Set<Long> triggeredNotifications = new HashSet<>();
	private final List<Integer> notificationTimes = List.of(60, 30, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1);

	private final World world;

	private final DungeonTeam team;
	private final int difficulty;
	private final DungeonTheme theme;
	private final DungeonObjective objective;

	private SimpleTask tickTask;
	private long secondsLeft;

	private SimpleTask teleportBackTask;
	private long secondsLeftTeleportBack;

	static {
		if (!WORLDS_DIRECTORY.exists()) {
			WORLDS_DIRECTORY.mkdirs();
		}
	}

	public DungeonWorld(final DungeonTeam team, final int difficulty, final DungeonTheme theme, final DungeonObjective objective) {
		final String worldName = "dungeons/dungeon_" + team.getId().toString().replace("-", "");
		this.team = team;
		this.difficulty = difficulty;
		this.theme = theme;
		this.objective = objective;

		this.team.setStatus(DungeonStatus.DUNGEON_GENERATING);
		this.team.setDungeon(this);

		final WorldCreator worldCreator = WorldCreator.name(worldName);
		worldCreator.environment(World.Environment.NETHER);
		worldCreator.generator(new DungeonGenerator(this.theme, this.objective));

		this.world = Bukkit.createWorld(worldCreator);
		if (this.world == null) {
			Common.log("Failed to create dungeon world for team " + team + "!", LogLevel.ERROR);
			return;
		}

		this.setGameRules();
		this.objective.setDifficulty(difficulty);
		this.objective.setDungeonWorld(this);
		this.objective.setDungeonTeam(team);
		this.objective.init();

		final Title title = Title.title(
				Caps.get("Generating...", Color.PURPLE),
				Caps.get("Please Wait...", Color.GRAY),
				Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(GENERATION_TIME - 1), Duration.ofMillis(500))
		);

		this.team.getPlayers().forEach(player -> player.showTitle(title));

		Concurrency.runLater(GENERATION_TIME * 20, () -> {
			this.team.getPlayers().forEach(player -> player.teleport(new Location(this.world, 8, 146, 10.5, -180, 0)));
			this.team.setStatus(DungeonStatus.DUNGEON_OPEN);
			this.objective.addHuds(this.team.getPlayers());

			final DungeonTeamJoinEvent event = new DungeonTeamJoinEvent(this.team, this);
			Bukkit.getPluginManager().callEvent(event);
		});

		Bukkit.getPluginManager().registerEvents(this, MinesOfMystery.getInstance());
		Bukkit.getPluginManager().registerEvents(objective, MinesOfMystery.getInstance());
	}

	private void setGameRules() {
		this.world.setAutoSave(true);
		this.world.setDifficulty(Difficulty.HARD);
		this.world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
		this.world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
		this.world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
		this.world.setGameRule(GameRule.DO_MOB_LOOT, false);
		this.world.setGameRule(GameRule.DO_TILE_DROPS, false);
		this.world.setGameRule(GameRule.DO_FIRE_TICK, false);
		this.world.setGameRule(GameRule.DO_ENTITY_DROPS, false);
		this.world.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
		this.world.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
		this.world.setGameRule(GameRule.FALL_DAMAGE, false);
		this.world.setGameRule(GameRule.NATURAL_REGENERATION, false);
		this.world.setGameRule(GameRule.SPAWN_RADIUS, 0);
		this.world.setGameRule(GameRule.GLOBAL_SOUND_EVENTS, false);
		this.world.setThundering(false);
		this.world.setStorm(false);
	}

	public void startDungeonTick(final int timeLeft) {
		if (this.tickTask != null)
			return;

		this.secondsLeft = timeLeft;
		final LocalDateTime expires = LocalDateTime.now().plusSeconds(timeLeft);
		this.tickTask = Concurrency.runTimerAsync(20, () -> {
			if (this.world.getPlayers().isEmpty())
				return;

			this.secondsLeft = Duration.between(LocalDateTime.now(), expires).getSeconds();
			if (this.secondsLeft < 0) {
				Concurrency.runLater(this::handleOutOfTime);
				this.tickTask.cancel();
				return;
			}

			this.team.setTimeLeft(this.secondsLeft);

			if (this.notificationTimes.contains((int) this.secondsLeft) && this.triggeredNotifications.add(this.secondsLeft)) {
				final float pitch = this.secondsLeft > 10 ? 0.9f : 1.3f;
				this.team.getPlayers().forEach(player -> player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 2, pitch));
			}
		});
	}

	private void handleOutOfTime() {
		this.world.getPlayers().forEach(p -> p.setHealth(0));
		this.team.getPlayers().forEach(player -> {
			final DungeonPlayerLeaveEvent event = new DungeonPlayerLeaveEvent(player, this, this.getTeam(), 0, DungeonPlayerLeaveEvent.Reason.OUT_OF_TIME);
			Bukkit.getPluginManager().callEvent(event);
		});
	}

	private void startTeleportBackTask(final int secondsUntilTeleport) {
		if (this.teleportBackTask != null)
			return;

		this.team.setStatus(DungeonStatus.DUNGEON_COMPLETED);
		this.tickTask.cancel();

		this.secondsLeftTeleportBack = secondsUntilTeleport;
		final LocalDateTime expires = LocalDateTime.now().plusSeconds(secondsUntilTeleport);
		this.teleportBackTask = Concurrency.runTimerAsync(20, () -> {
			if (this.world.getPlayers().isEmpty())
				return;

			this.secondsLeftTeleportBack = Duration.between(LocalDateTime.now(), expires).getSeconds();
			if (this.secondsLeftTeleportBack < 1) {
				Concurrency.runLater(() -> {
					for (final Player player : this.world.getPlayers()) {
						final DungeonPlayerLeaveEvent leaveEvent = new DungeonPlayerLeaveEvent(player, this, this.getTeam(), this.secondsLeft, DungeonPlayerLeaveEvent.Reason.COMPLETED);
						Bukkit.getPluginManager().callEvent(leaveEvent);
					}
				});
				this.teleportBackTask.cancel();
				return;
			}

			this.team.setTimeLeft(this.secondsLeftTeleportBack);
		});
	}

	/**
	 * Check whether the player is inside the portal and wants to leave
	 */
	@EventHandler
	public void onMove(final PlayerMoveEvent event) {
		if (!event.hasChangedPosition() || !event.getPlayer().getWorld().getName().equals(this.world.getName()))
			return;

		final Block block = event.getTo().getBlock();
		if (block.getType() != CustomBlock.NON_SOLID_PLACEHOLDER)
			return;

		if (CustomBlocks.isCustomBlock(block, CustomBlocks.DUNGEON_PORTAL_EW, CustomBlocks.DUNGEON_PORTAL_NS)) {
			if (this.team.getStatus() == DungeonStatus.DUNGEON_COMPLETED)
				return;

			final DungeonPlayerLeaveEvent leaveEvent = new DungeonPlayerLeaveEvent(
					event.getPlayer(),
					this,
					this.getTeam(),
					this.secondsLeft,
					DungeonPlayerLeaveEvent.Reason.PLAYER_LEFT
			);
			Bukkit.getPluginManager().callEvent(leaveEvent);
		}
	}

	/**
	 * Handle player death
	 */
	@EventHandler
	public void onKill(final EntityDeathEvent event) {
		if (!event.getEntity().getWorld().getName().equals(this.world.getName()))
			return;

		final Entity entity = event.getEntity();
		if (entity instanceof final Player player) {
			final DungeonPlayerLeaveEvent leaveEvent = new DungeonPlayerLeaveEvent(player, this, this.getTeam(), this.secondsLeft, DungeonPlayerLeaveEvent.Reason.DEFEATED);
			Bukkit.getPluginManager().callEvent(leaveEvent);
		}
	}

	/*
	 * Handle dungeon objective completion
	 */
	@EventHandler
	public void onObjectiveComplete(final DungeonObjectiveCompletedEvent event) {
		Message.broadcast(Component.text(this.team.getLeader().getUsername() + "'s").color(Color.ORANGE).append(Component.text(" team completed the dungeon!").color(Color.GREEN)));
		this.startTeleportBackTask(20);
		this.team.getPlayers().forEach(player -> player.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1));

		this.objective.afterComplete();
		this.objective.removeHuds(this.team.getPlayers());
	}

	public void deleteDungeon() {
		if (this.team != null) {
			this.team.setStatus(DungeonStatus.NO_DUNGEON);
			this.objective.afterComplete();
			this.objective.removeHuds(this.team.getPlayers());
		}

		for (final Player player : this.world.getPlayers()) {
			if (player.isDead()) {
				player.spigot().respawn();
			}

			player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
		}

		Bukkit.unloadWorld(this.world, false);
		Concurrency.runLater(100, () -> {
			if (this.team != null)
				this.team.setDungeon(null);

			try {
				FileUtils.deleteDirectory(new File(this.world.getName()));
				Common.log("Deleted dungeon world " + this.world.getName() + "!");
			} catch (final IOException e) {
				Common.error("Failed to delete dungeon world " + this.world.getName() + "!", e);
			}
		});
	}
}
