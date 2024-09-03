package eu.playsc.minesofmystery.dungeon;

import eu.playsc.minesofmystery.common.Color;
import eu.playsc.minesofmystery.common.Formatter;
import eu.playsc.minesofmystery.common.concurrency.Concurrency;
import eu.playsc.minesofmystery.common.concurrency.SimpleTask;
import eu.playsc.minesofmystery.custom.fonts.Branding;
import eu.playsc.minesofmystery.custom.fonts.Caps;
import eu.playsc.minesofmystery.custom.fonts.Icons;
import eu.playsc.minesofmystery.dungeon.world.DungeonWorld;
import eu.playsc.minesofmystery.model.PlayerData;
import fr.mrmicky.fastboard.adventure.FastBoard;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class DungeonTeam implements Listener {
	private static final ConcurrentHashMap<UUID, DungeonTeam> TEAMS = new ConcurrentHashMap<>();

	private final UUID id;
	private final PlayerData leader;
	private final List<PlayerData> members = new ArrayList<>();
	private final ConcurrentHashMap<UUID, FastBoard> boards = new ConcurrentHashMap<>();

	@Setter
	private DungeonStatus status = DungeonStatus.NO_DUNGEON;
	@Setter
	private DungeonWorld dungeon = null;
	@Setter
	private long timeLeft = 0;

	private SimpleTask scoreboardTask;

	public static DungeonTeam get(final Player player) {
		if (TEAMS.containsKey(player.getUniqueId()))
			return TEAMS.get(player.getUniqueId());

		return new DungeonTeam(player);
	}

	public DungeonTeam(final Player player) {
		this.id = player.getUniqueId();
		this.leader = PlayerData.from(player);
		this.addMember(player);
		this.tickScoreboard();
	}

	public List<Player> getPlayers() {
		final List<Player> players = new ArrayList<>();
		for (final PlayerData playerData : this.members) {
			final Player player = Bukkit.getPlayer(playerData.getUuid());
			if (player != null && player.isOnline()) {
				players.add(player);
			}
		}

		return players;
	}

	public void addMember(final Player player) {
		if (this.members.stream().anyMatch(p -> p.getUuid().equals(player.getUniqueId())))
			return;

		this.members.add(PlayerData.from(player));

		if (PlayerData.from(player).isEnderDragonDefeated())
			this.boards.put(player.getUniqueId(), this.newBoard(player));

		TEAMS.put(player.getUniqueId(), this);
	}

	public void removeMember(final Player player) {
		this.members.remove(PlayerData.from(player));
		this.boards.remove(player.getUniqueId());

		TEAMS.remove(player.getUniqueId());
		new DungeonTeam(player);
	}

	public void toggleScoreboard(final Player player) {
		final FastBoard board = this.getBoard(player);
		if (!board.isDeleted()) {
			board.delete();
			this.boards.remove(player.getUniqueId());
		} else {
			this.boards.put(player.getUniqueId(), this.newBoard(player));
		}
	}

	public boolean hasActiveBoard(final Player player) {
		return this.boards.containsKey(player.getUniqueId());
	}

	public FastBoard getBoard(final Player player) {
		final FastBoard board = this.boards.get(player.getUniqueId());
		if (board == null || !board.getPlayer().equals(player)) {
			this.newBoard(player);
		}

		return this.boards.get(player.getUniqueId());
	}

	private void tickScoreboard() {
		if (this.scoreboardTask != null)
			return;

		this.scoreboardTask = Concurrency.runTimerAsync(1, () -> {
			for (final PlayerData playerData : this.members) {
				final Player player = Bukkit.getPlayer(playerData.getUuid());
				if (player == null || !player.isOnline() || !playerData.isEnderDragonDefeated())
					continue;

				final FastBoard board = this.getBoard(player);
				if (board.isDeleted())
					continue;

				switch (this.status) {
					case DUNGEON_OPEN -> board.updateLines(this.getDungeonOpenLines(player));
					case DUNGEON_COMPLETED -> board.updateLines(this.getDungeonCompletedLines(player));
					default -> board.updateLines(this.getDefaultLines(player));
				}
			}
		});
	}

	private List<Component> getDungeonOpenLines(final Player player) {
		return List.of(
				Component.empty(),
				this.getTeamComponent(player),
				Component.empty(),
				this.getDifficultyComponent(),
				this.getStatusComponent(),
				this.getThemeComponent(),
				Component.empty()
		);
	}

	private List<Component> getDungeonCompletedLines(final Player player) {
		return List.of(
				Component.empty(),
				this.getTeamComponent(player),
				Component.empty(),
				this.getStatusComponent(),
				this.getTeleportComponent(),
				Component.empty()
		);
	}

	private List<Component> getDefaultLines(final Player player) {
		return List.of(
				Component.empty(),
				this.getTeamComponent(player),
				Component.empty(),
				this.getStatusComponent(),
				Component.empty()
		);
	}

	private Component getTeamComponent(final Player player) {
		Component icon = Component.text(" ").append(Icons.TEAM.getWithSpace());
		if (this.members.size() == 1) {
			return icon.append(Caps.get("no team", Color.GRAY));
		}

		icon = icon.append(Caps.get("team:", Color.BLUE));
		Component teamComponent = Component.empty();
		final List<PlayerData> members = this.members.stream()
				.filter(p -> !p.getUuid().equals(player.getUniqueId()))
				.toList();

		for (final PlayerData member : members) {
			teamComponent = teamComponent.append(
					Caps.get(" - ", Color.GRAY)
							.append(Caps.get(member.getUsername(), Color.ORANGE))
							.append(Component.newline())
			);
		}

		return icon.append(teamComponent);
	}

	private Component getStatusComponent() {
		return switch (this.status) {
			case NO_DUNGEON -> {
				final Component icon = Component.text(" ").append(this.status.getIcon());
				yield icon.append(Caps.get("no dungeon", Color.GRAY));
			}
			case DUNGEON_GENERATING -> {
				final Component icon = Component.text(" ").append(this.status.getIcon());
				yield icon.append(Caps.get("generating...", Color.PURPLE));
			}
			case DUNGEON_OPEN -> {
				final Component icon = Component.text(" ").append(this.status.getIcon());
				yield icon
						.append(
								new Formatter.Time(this.timeLeft)
										.inCaps(true)
										.show(false, false, true, true)
										.withFormat(Formatter.Time.Format.DECIMAL)
										.withLowTimeHighlight(true)
										.withLowTimeThreshold(60)
										.build()
						);
			}
			case DUNGEON_COMPLETED -> {
				final Component icon = Component.text(" ").append(this.status.getIcon());
				yield icon.append(Caps.get("completed", Color.GREEN));
			}
		};
	}

	private Component getDifficultyComponent() {
		return Component.text(" ")
				.append(Component.text("┌ ").color(Color.GRAY)
						.append(Caps.get("difficulty: ", Color.GRAY)).append(Caps.get(this.dungeon.getDifficulty(), Color.RED)));
	}

	private Component getThemeComponent() {
		return Component.text(" ")
				.append(Component.text("└ ").color(Color.GRAY)
						.append(this.dungeon.getTheme().getDisplayNameCaps()));
	}

	private Component getTeleportComponent() {
		return Component.text(" ")
				.append(Component.text("└ ").color(Color.GRAY)
						.append(Caps.get("leaving in ", Color.GRAY).append(
								new Formatter.Time(this.timeLeft)
										.inCaps(true)
										.show(false, false, false, true)
										.withFormat(Formatter.Time.Format.DECIMAL)
										.build()
						)));
	}

	private FastBoard newBoard(final Player player) {
		final FastBoard board = new FastBoard(player);
		board.updateTitle(Branding.LOGO.get());

		this.boards.put(player.getUniqueId(), board);
		return board;
	}
}
