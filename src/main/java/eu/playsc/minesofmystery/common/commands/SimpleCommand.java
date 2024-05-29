package eu.playsc.minesofmystery.common.commands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkit;
import dev.jorel.commandapi.CommandAPICommand;
import eu.playsc.minesofmystery.common.Common;
import eu.playsc.minesofmystery.common.LogLevel;
import eu.playsc.minesofmystery.common.Message;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.command.CommandSender;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public abstract class SimpleCommand {
	private static boolean commandsRegistered = false;

	public static void registerCommands() {
		if (commandsRegistered) {
			Common.log("Commands have already been registered!", LogLevel.ERROR);
			return;
		}

		Common.log("Registering commands...");
		final List<SimpleCommand> registeredCommands = new ArrayList<>();

		final Reflections reflection = new Reflections("eu.playsc.minesofmystery.commands");
		reflection.getSubTypesOf(SimpleCommand.class).forEach(command -> {
			try {
				final SimpleCommand simpleCommand = command.getDeclaredConstructor().newInstance();

				for (final SimpleCommand.VanillaUnregister vanillaCommand : simpleCommand.unregisterVanillaCommands()) {
					unregisterVanillaCommand(vanillaCommand);
				}

				for (final SimpleCommand.BukkitUnregister bukkitCommand : simpleCommand.unregisterBukkitCommands()) {
					unregisterBukkitCommand(bukkitCommand);
				}

				// Actually register the command
				simpleCommand.getCommand().register();
				registeredCommands.add(simpleCommand);
			} catch (final Exception e) {
				Common.log("Failed to register command: " + command.getSimpleName() + "!", e);
			}
		});

		commandsRegistered = true;
		Common.log("Registered " + registeredCommands.size() + " commands.", LogLevel.SUCCESS);
	}

	private static void unregisterVanillaCommand(final SimpleCommand.VanillaUnregister command) {
		CommandAPI.unregister(command.getCommand(), command.isUnregisterNamespaces());
	}

	private static void unregisterBukkitCommand(final SimpleCommand.BukkitUnregister command) {
		CommandAPIBukkit.unregister(command.getCommand(), command.isUnregisterNamespaces(), command.isUnregisterBukkit());
	}

	public abstract CommandAPICommand getCommand();

	public List<VanillaUnregister> unregisterVanillaCommands() {
		return List.of();
	}

	public List<BukkitUnregister> unregisterBukkitCommands() {
		return List.of();
	}

	public void success(final CommandSender sender, final String message) {
		Message.success(sender, message);
	}

	public void error(final CommandSender sender, final String message) {
		Message.error(sender, message);
	}

	public void hardError(final CommandSender sender, final String message) {
		Message.hardError(sender, message);
	}

	public void warning(final CommandSender sender, final String message) {
		Message.warning(sender, message);
	}

	public void info(final CommandSender sender, final String message) {
		Message.info(sender, message);
	}

	@Getter
	public static class VanillaUnregister {
		private final String command;
		private final boolean unregisterNamespaces;

		public VanillaUnregister(final String command) {
			this.command = command;
			this.unregisterNamespaces = false;
		}

		public VanillaUnregister(final String command, final boolean unregisterNamespaces) {
			this.command = command;
			this.unregisterNamespaces = unregisterNamespaces;
		}
	}

	@Getter
	public static class BukkitUnregister {
		private final String command;
		private final boolean unregisterNamespaces;
		private final boolean unregisterBukkit;

		public BukkitUnregister(final String command) {
			this(command, true, true);
		}

		public BukkitUnregister(final String command, final boolean unregisterNamespaces, final boolean unregisterBukkit) {
			this.command = command;
			this.unregisterNamespaces = unregisterNamespaces;
			this.unregisterBukkit = unregisterBukkit;
		}
	}
}
