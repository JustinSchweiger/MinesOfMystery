package eu.playsc.minesofmystery.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import eu.playsc.minesofmystery.MinesOfMystery;
import eu.playsc.minesofmystery.common.commands.SimpleCommand;
import eu.playsc.minesofmystery.custom.manager.CustomBlockManager;
import eu.playsc.minesofmystery.custom.manager.CustomItemManager;
import eu.playsc.minesofmystery.dungeon.DungeonTheme;
import eu.playsc.minesofmystery.dungeon.handlers.DungeonHandler;
import eu.playsc.minesofmystery.dungeon.objectives.DungeonObjective;
import eu.playsc.minesofmystery.model.PlayerData;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MOMCommand extends SimpleCommand {
	@Override
	public CommandAPICommand getCommand() {
		return new CommandAPICommand("mom")
				.withAliases("minesofmystery")
				.withSubcommand(new CommandAPICommand("reload")
						.executes((commandSender, commandArguments) -> {
							MinesOfMystery.getResourcepackManager().reload();
						})
				).withSubcommand(new CommandAPICommand("give")
						.withArguments(new NamespacedKeyArgument("key").replaceSuggestions(
								ArgumentSuggestions.strings(Stream.concat(
										CustomBlockManager.get().getRegisteredBlocks().stream(),
										CustomItemManager.get().getRegisteredItems().stream()
								).collect(Collectors.toList()))
						)).executesPlayer((player, args) -> {
							final NamespacedKey key = (NamespacedKey) args.args()[0];

							CustomBlockManager.get().getCustomBlock(key).ifPresent(customBlock -> player.getInventory().addItem(customBlock.getItemStack()));
							CustomItemManager.get().getCustomItem(key).ifPresent(customItem -> player.getInventory().addItem(customItem.getItemStack()));
						})
				).withSubcommand(new CommandAPICommand("giveKey")
						.withOptionalArguments(new PlayerArgument("target"))
						.withOptionalArguments(new IntegerArgument("difficulty", 1))
						.withOptionalArguments(new MultiLiteralArgument("theme", DungeonTheme.getNames()))
						.withOptionalArguments(new MultiLiteralArgument("objective", DungeonObjective.getNames()))
						.executes((sender, args) -> {
							final Player target = (Player) args.getOrDefault("target", sender);
							final int difficulty = (int) args.getOrDefault("difficulty", PlayerData.from(target).getDungeonLevel());
							final DungeonTheme theme = DungeonTheme.get((String) args.getOrDefault("theme", DungeonTheme.getRandomTheme().getName()));
							final DungeonObjective objective = DungeonObjective.getObjective(
									new NamespacedKey(
											"minesofmystery",
											(String) args.getOrDefault("objective", DungeonObjective.getRandomObjective().getKey().getKey())
									)
							);

							target.getInventory().addItem(DungeonHandler.get().getKey(difficulty, theme, objective));
						})
				);
	}
}
