package eu.playsc.minesofmystery.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Formatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Common {
	public static final String LOG_PREFIX = "§8[§6MinesOfMystery§8] ";

	public static void log(final String message) {
		log(message, LogLevel.INFO, true);
	}

	public static void log(final String message, final LogLevel logLevel) {
		log(message, logLevel, true);
	}

	public static void log(final String message, final boolean showPrefix) {
		log(message, LogLevel.INFO, showPrefix);
	}

	private static void log(String message, final LogLevel logLevel, final boolean showPrefix) {
		final CommandSender console = Bukkit.getConsoleSender();

		if (message == null || message.isEmpty()) {
			return;
		}

		if (showPrefix) {
			message = LOG_PREFIX + logLevel.getColor() + message;
		}

		console.sendMessage(logLevel.getColor() + message);
	}

	public static String escapeUnicode(final String input) {
		final StringBuilder b = new StringBuilder(input.length());
		final Formatter f = new Formatter(b);
		for (final char c : input.toCharArray()) {
			if (c < 128) {
				b.append(c);
			} else {
				f.format("\\u%04x", (int) c);
			}
		}
		return b.toString();
	}

	public static void error(final String message, final Exception e) {
		throw new RuntimeException(message, e);
	}

	public static void error(final Throwable e) {
		throw new RuntimeException(e);
	}
}
