package eu.playsc.minesofmystery.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Common {
	public static final String LOG_PREFIX = "§8[§6MinesOfMystery§8] ";

	public static void log(final String message, final Exception e) {
		log(message + " " + e.getMessage(), LogLevel.EXCEPTION, true);
	}

	public static void log(final String message, final Exception e, boolean showPrefix) {
		log(message + " " + e.getMessage(), LogLevel.EXCEPTION, showPrefix);
	}

	public static void log(final Exception e) {
		log(e.getMessage(), LogLevel.EXCEPTION, true);
	}

	public static void log(Exception e, boolean showPrefix) {
		log(e.getMessage(), LogLevel.EXCEPTION, showPrefix);
	}

	public static void log(final String message) {
		log(message, LogLevel.INFO, true);
	}

	public static void log(final String message, final LogLevel logLevel) {
		log(message, logLevel, true);
	}

	public static void log(final String message, boolean showPrefix) {
		log(message, LogLevel.INFO, showPrefix);
	}

	private static void log(String message, final LogLevel logLevel, final boolean showPrefix) {
		final CommandSender console = Bukkit.getConsoleSender();
		Valid.checkNotNull(console, "Console sender is null!");

		if (message == null || message.isEmpty()) {
			return;
		}

		if (showPrefix) {
			message = LOG_PREFIX + logLevel.getColor() + message;
		}

		console.sendMessage(logLevel.getColor() + message);
	}

	public static void throwError(final String message) {
		throw new RuntimeException(message);
	}

	public static void throwError(final String message, final Exception e) {
		throw new RuntimeException(message, e);
	}

	public static void throwError(final Exception e) {
		throw new RuntimeException(e);
	}
}
