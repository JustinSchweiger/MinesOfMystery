package eu.playsc.minesofmystery.commands;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import eu.playsc.minesofmystery.common.Message;
import eu.playsc.minesofmystery.common.commands.SimpleCommand;
import eu.playsc.minesofmystery.dungeon.*;
import eu.playsc.minesofmystery.dungeon.entity.DungeonEntity;
import eu.playsc.minesofmystery.dungeon.handlers.DungeonHandler;
import eu.playsc.minesofmystery.dungeon.handlers.DungeonRegionHandler;
import eu.playsc.minesofmystery.dungeon.objectives.KillMonstersObjective;
import eu.playsc.minesofmystery.dungeon.world.DungeonWorld;
import org.bukkit.Location;
import org.bukkit.Material;

public class DungeonCommand extends SimpleCommand {
	@Override
	public CommandAPICommand getCommand() {
		return new CommandAPICommand("dungeon")
				.withSubcommand(new CommandAPICommand("region")
						.withSubcommand(new CommandAPICommand("save")
								.withArguments(new MultiLiteralArgument("type", DungeonRegionType.getNames()))
								.withArguments(new MultiLiteralArgument("theme", DungeonTheme.getNames()))
								.withArguments(new StringArgument("name"))
								.executesPlayer((player, args) -> {
									if (!DungeonRegionHandler.get().canSaveRegion(player)) {
										Message.error(player, "You need to select a region first!");
										return;
									}

									final String name = (String) args.get("name");
									final DungeonRegionType type = DungeonRegionType.get((String) args.get("type"));
									final DungeonTheme theme = DungeonTheme.get((String) args.get("theme"));

									DungeonRegionHandler.get().saveRegion(
											name,
											type,
											theme,
											WorldEdit.getInstance().getSessionManager().getIfPresent(BukkitAdapter.adapt(player)).getSelection(),
											player
									);
								})
						)
						.withSubcommand(new CommandAPICommand("load")
								.withArguments(new MultiLiteralArgument("type", DungeonRegionType.getNames()))
								.withArguments(new MultiLiteralArgument("theme", DungeonTheme.getNames()))
								.withArguments(new StringArgument("name").replaceSuggestions(
										ArgumentSuggestions.strings(info -> DungeonRegionHandler.get().getLoadableRegions(
												DungeonRegionType.get((String) info.previousArgs().get("type")),
												DungeonTheme.get((String) info.previousArgs().get("theme"))
										))
								)).executesPlayer((player, args) -> {
									final String name = (String) args.get("name");
									final DungeonRegionType type = DungeonRegionType.get((String) args.get("type"));
									final DungeonTheme theme = DungeonTheme.get((String) args.get("theme"));

									DungeonRegionHandler.get().loadRegion(
											name,
											type,
											theme,
											player.getLocation()
									);
								})
						)
				).withSubcommand(new CommandAPICommand("team")
						.withSubcommand(new CommandAPICommand("scoreboard")
								.executesPlayer((player, args) -> {
									final DungeonTeam team = DungeonTeam.get(player);
									team.toggleScoreboard(player);

									Message.info(player, "Scoreboard " + (team.hasActiveBoard(player) ? "enabled" : "disabled"));
								})
						)
				).withSubcommand(new CommandAPICommand("spawnMobs")
						.withArguments(new IntegerArgument("min", 1))
						.withArguments(new IntegerArgument("max", 1))
						.executesCommandBlock((commandBlock, args) -> {
							final Location location = commandBlock.getBlock().getLocation().clone();
							final int min = (int) args.get("min");
							final int max = (int) args.get("max");

							final DungeonWaveSpawner spawner = new DungeonWaveSpawner.Builder()
									.waves(
											new DungeonWaveSpawner.Wave(DungeonEntities.ZOMBIE_T1, min, max),
											new DungeonWaveSpawner.Wave(DungeonEntities.ZOMBIE_T2, min, max),
											new DungeonWaveSpawner.Wave(DungeonEntities.ZOMBIE_T3, min, max)
									).waveSpawnRadius(4)
									.maxWaveSize(5)
									.build();

							spawner.spawn(location.clone().add(0, 2, 0));
							location.getWorld().setBlockData(location, Material.AIR.createBlockData());
						})
				).withSubcommand(new CommandAPICommand("spawnBoss")
						.executesCommandBlock((commandBlock, args) -> {
							final Location location = commandBlock.getBlock().getLocation().clone();
							final DungeonWorld dungeon = DungeonHandler.get().getDungeon(location.getWorld()).orElse(null);
							if (dungeon == null || !(dungeon.getObjective() instanceof final KillMonstersObjective objective))
								return;

							if (!objective.isBossSpawnable())
								return;

							location.getWorld().setBlockData(location, Material.AIR.createBlockData());
							objective.setBossSpawned(true);

							final DungeonEntity entity = DungeonEntities.BOSS_T1.createEntity();
							entity.spawn(location.add(0, 3, 0));
						})
				);
	}
}
