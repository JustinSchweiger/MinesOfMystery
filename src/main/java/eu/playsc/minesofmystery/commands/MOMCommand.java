package eu.playsc.minesofmystery.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.NamespacedKeyArgument;
import eu.playsc.minesofmystery.MinesOfMystery;
import eu.playsc.minesofmystery.common.commands.SimpleCommand;
import eu.playsc.minesofmystery.common.custom.manager.CustomBlockManager;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

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
						.withArguments(new NamespacedKeyArgument("block").replaceSuggestions(ArgumentSuggestions.strings(CustomBlockManager.get().getRegisteredBlocks())))
						.executesPlayer((player, args) -> {
							final ItemStack item = CustomBlockManager.get().getCustomBlock((NamespacedKey) args.args()[0]).get().getItemStack();
							player.getInventory().addItem(item);
						})
				);
	}
}
