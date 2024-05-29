package eu.playsc.minesofmystery.common;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public class Message {
	public static void success(final CommandSender sender, final String message) {
		final Component component = Symbol.SUCCESS.get(Colors.SUCCESS_SYMBOL).append(Component.text(message).color(Colors.SUCCESS));
		sender.sendMessage(component);
	}

	public static void error(final CommandSender sender, final String message) {
		final Component component = Symbol.ERROR.get(Colors.ERROR_SYMBOL).append(Component.text(message).color(Colors.ERROR));
		sender.sendMessage(component);
	}

	public static void hardError(final CommandSender sender, final String message) {
		final Component component = Symbol.ERROR.get(Colors.HARD_ERROR_SYMBOL).append(Component.text(message).color(Colors.HARD_ERROR));
		sender.sendMessage(component);
	}

	public static void warning(final CommandSender sender, final String message) {
		final Component component = Symbol.WARNING.get(Colors.WARNING_SYMBOL).append(Component.text(message).color(Colors.WARNING));
		sender.sendMessage(component);
	}

	public static void info(final CommandSender sender, final String message) {
		final Component component = Symbol.INFO.get(Colors.INFO_SYMBOL).append(Component.text(message).color(Colors.INFO));
		sender.sendMessage(component);
	}
}
