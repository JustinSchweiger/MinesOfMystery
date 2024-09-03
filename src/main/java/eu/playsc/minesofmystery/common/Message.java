package eu.playsc.minesofmystery.common;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Message {
	public static void success(final CommandSender sender, final String message) {
		final Component component = Symbol.SUCCESS.get(Color.SUCCESS_SYMBOL).append(Component.text(message).color(Color.SUCCESS));
		sender.sendMessage(component);
	}

	public static void success(final CommandSender sender, final Component component) {
		final Component newComponent = Symbol.SUCCESS.get(Color.SUCCESS_SYMBOL).append(component);
		sender.sendMessage(newComponent);
	}

	public static void error(final CommandSender sender, final String message) {
		final Component component = Symbol.ERROR.get(Color.ERROR_SYMBOL).append(Component.text(message).color(Color.ERROR));
		sender.sendMessage(component);
	}

	public static void hardError(final CommandSender sender, final String message) {
		final Component component = Symbol.ERROR.get(Color.HARD_ERROR_SYMBOL).append(Component.text(message).color(Color.HARD_ERROR));
		sender.sendMessage(component);
	}

	public static void warning(final CommandSender sender, final String message) {
		final Component component = Symbol.WARNING.get(Color.WARNING_SYMBOL).append(Component.text(message).color(Color.WARNING));
		sender.sendMessage(component);
	}

	public static void info(final CommandSender sender, final String message) {
		final Component component = Symbol.INFO.get(Color.INFO_SYMBOL).append(Component.text(message).color(Color.INFO));
		sender.sendMessage(component);
	}

	public static void broadcast(final Component message) {
		for (final Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage(message);
		}

		Bukkit.getConsoleSender().sendMessage(message);
	}
}
