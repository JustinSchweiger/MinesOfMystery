package eu.playsc.minesofmystery.common;

import eu.playsc.minesofmystery.custom.fonts.Caps;
import eu.playsc.minesofmystery.custom.fonts.Icons;
import lombok.Getter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.NamespacedKey;

public class Formatter {
	public static class Location {
		private final org.bukkit.Location location;

		private boolean caps = false;
		private boolean showWorld = false;

		private TextColor worldColor = Color.WORLD;
		private TextColor xColor = Color.X;
		private TextColor yColor = Color.Y;
		private TextColor zColor = Color.Z;

		public Location(final org.bukkit.Location location) {
			this.location = location;
		}

		public Location inCaps(final boolean inCaps) {
			this.caps = inCaps;
			return this;
		}

		public Location showWorld(final boolean showWorld) {
			this.showWorld = showWorld;
			return this;
		}

		public Location withWorldColor(final TextColor worldColor) {
			this.worldColor = worldColor;
			return this;
		}

		public Location withXColor(final TextColor xColor) {
			this.xColor = xColor;
			return this;
		}

		public Location withYColor(final TextColor yColor) {
			this.yColor = yColor;
			return this;
		}

		public Location withZColor(final TextColor zColor) {
			this.zColor = zColor;
			return this;
		}

		public Location withColors(final TextColor worldColor, final TextColor xColor, final TextColor yColor, final TextColor zColor) {
			this.worldColor = worldColor;
			this.xColor = xColor;
			this.yColor = yColor;
			this.zColor = zColor;
			return this;
		}

		public Location withColor(final TextColor color) {
			this.worldColor = color;
			this.xColor = color;
			this.yColor = color;
			this.zColor = color;
			return this;
		}

		public Component build() {
			final String world = this.showWorld ? this.location.getWorld().getName() : "";
			final String x = String.valueOf(this.location.getBlockX());
			final String y = String.valueOf(this.location.getBlockY());
			final String z = String.valueOf(this.location.getBlockZ());

			final Component xComponent = this.caps ? Caps.get(x, this.xColor) : Component.text(x).color(this.xColor);
			final Component yComponent = this.caps ? Caps.get(y, this.yColor) : Component.text(y).color(this.yColor);
			final Component zComponent = this.caps ? Caps.get(z, this.zColor) : Component.text(z).color(this.zColor);
			final Component worldComponent = this.caps ? Caps.get(world, this.worldColor) : Component.text(world).color(this.worldColor);

			Component component = Component.empty();
			component = component
					.append(Component.text("(").color(Color.GRAY))
					.append(xComponent)
					.append(Component.text(", ").color(Color.GRAY))
					.append(yComponent)
					.append(Component.text(", ").color(Color.GRAY))
					.append(zComponent);

			if (this.showWorld) {
				component = component
						.append(Component.text(" in world ").color(Color.GRAY))
						.append(worldComponent);
			}

			return component.append(Component.text(")").color(Color.GRAY));
		}
	}

	public static class Time {
		private boolean showDays = false;
		private boolean showHours = true;
		private boolean showMinutes = true;
		private boolean showSeconds = true;
		private boolean caps = false;
		private boolean lowTimeHighlight = false;

		private final long time;
		private long lowTimeThreshold = 60;

		private Format format = Format.SHORT;

		private TextColor daysColor = Color.DAYS;
		private TextColor hoursColor = Color.HOURS;
		private TextColor minutesColor = Color.MINUTES;
		private TextColor secondsColor = Color.SECONDS;

		private TextColor lowTimeDaysColor = Color.LOW_TIME_DAYS;
		private TextColor lowTimeHoursColor = Color.LOW_TIME_HOURS;
		private TextColor lowTimeMinutesColor = Color.LOW_TIME_MINUTES;
		private TextColor lowTimeSecondsColor = Color.LOW_TIME_SECONDS;

		public enum Format {
			DECIMAL,
			LONG,
			SHORT
		}

		public Time(final long time) {
			this.time = time;
		}

		public Time showDays(final boolean showDays) {
			this.showDays = showDays;
			return this;
		}

		public Time showHours(final boolean showHours) {
			this.showHours = showHours;
			return this;
		}

		public Time showMinutes(final boolean showMinutes) {
			this.showMinutes = showMinutes;
			return this;
		}

		public Time showSeconds(final boolean showSeconds) {
			this.showSeconds = showSeconds;
			return this;
		}

		public Time show(final boolean showDays, final boolean showHours, final boolean showMinutes, final boolean showSeconds) {
			this.showDays = showDays;
			this.showHours = showHours;
			this.showMinutes = showMinutes;
			this.showSeconds = showSeconds;
			return this;
		}

		public Time inCaps(final boolean inCaps) {
			this.caps = inCaps;
			return this;
		}

		public Time withFormat(final Format format) {
			this.format = format;
			return this;
		}

		public Time withDaysColor(final TextColor daysColor) {
			this.daysColor = daysColor;
			return this;
		}

		public Time withHoursColor(final TextColor hoursColor) {
			this.hoursColor = hoursColor;
			return this;
		}

		public Time withMinutesColor(final TextColor minutesColor) {
			this.minutesColor = minutesColor;
			return this;
		}

		public Time withSecondsColor(final TextColor secondsColor) {
			this.secondsColor = secondsColor;
			return this;
		}

		public Time withColors(final TextColor daysColor, final TextColor hoursColor, final TextColor minutesColor, final TextColor secondsColor) {
			this.daysColor = daysColor;
			this.hoursColor = hoursColor;
			this.minutesColor = minutesColor;
			this.secondsColor = secondsColor;
			return this;
		}

		public Time withColor(final TextColor color) {
			this.daysColor = color;
			this.hoursColor = color;
			this.minutesColor = color;
			this.secondsColor = color;
			return this;
		}

		public Time withLowTimeHighlight(final boolean lowTimeHighlight) {
			this.lowTimeHighlight = lowTimeHighlight;
			return this;
		}

		public Time withLowTimeThreshold(final long lowTimeThreshold) {
			this.lowTimeThreshold = lowTimeThreshold;
			return this;
		}

		public Time withLowTimeDaysColor(final TextColor lowTimeDaysColor) {
			this.lowTimeDaysColor = lowTimeDaysColor;
			return this;
		}

		public Time withLowTimeHoursColor(final TextColor lowTimeHoursColor) {
			this.lowTimeHoursColor = lowTimeHoursColor;
			return this;
		}

		public Time withLowTimeMinutesColor(final TextColor lowTimeMinutesColor) {
			this.lowTimeMinutesColor = lowTimeMinutesColor;
			return this;
		}

		public Time withLowTimeSecondsColor(final TextColor lowTimeSecondsColor) {
			this.lowTimeSecondsColor = lowTimeSecondsColor;
			return this;
		}

		public Time withLowTimeColors(final TextColor lowTimeDaysColor, final TextColor lowTimeHoursColor, final TextColor lowTimeMinutesColor, final TextColor lowTimeSecondsColor) {
			this.lowTimeDaysColor = lowTimeDaysColor;
			this.lowTimeHoursColor = lowTimeHoursColor;
			this.lowTimeMinutesColor = lowTimeMinutesColor;
			this.lowTimeSecondsColor = lowTimeSecondsColor;
			return this;
		}

		public Component build() {
			final long days = this.time / 86400;
			final long hours = (this.time % 86400) / 3600;
			final long minutes = (this.time % 3600) / 60;
			final long seconds = this.time % 60;

			return switch (this.format) {
				case DECIMAL -> this.getDecimalFormat(days, hours, minutes, seconds);
				case LONG -> this.getLongFormat(days, hours, minutes, seconds);
				case SHORT -> this.getShortFormat(days, hours, minutes, seconds);
			};
		}

		private Component getDecimalFormat(final long days, final long hours, final long minutes, final long seconds) {
			Component finalComponent = Component.empty();

			final String daysString = String.format("%02d", days);
			final String hoursString = String.format("%02d", hours);
			final String minutesString = String.format("%02d", minutes);
			final String secondsString = String.format("%02d", seconds);

			TextColor daysColor = this.daysColor;
			TextColor hoursColor = this.hoursColor;
			TextColor minutesColor = this.minutesColor;
			TextColor secondsColor = this.secondsColor;

			if (this.lowTimeHighlight && this.time <= this.lowTimeThreshold) {
				daysColor = this.lowTimeDaysColor;
				hoursColor = this.lowTimeHoursColor;
				minutesColor = this.lowTimeMinutesColor;
				secondsColor = this.lowTimeSecondsColor;
			}

			if (this.showDays)
				finalComponent = finalComponent
						.append(this.caps ? Caps.get(daysString, daysColor) : Component.text(daysString).color(daysColor))
						.append(this.caps ? Caps.get(":", Color.GRAY) : Component.text(":").color(Color.GRAY));

			if (this.showHours)
				finalComponent = finalComponent
						.append(this.caps ? Caps.get(hoursString, hoursColor) : Component.text(hoursString).color(hoursColor))
						.append(this.caps ? Caps.get(":", Color.GRAY) : Component.text(":").color(Color.GRAY));

			if (this.showMinutes)
				finalComponent = finalComponent
						.append(this.caps ? Caps.get(minutesString, minutesColor) : Component.text(minutesString).color(minutesColor))
						.append(this.caps ? Caps.get(":", Color.GRAY) : Component.text(":").color(Color.GRAY));

			if (this.showSeconds)
				finalComponent = finalComponent
						.append(this.caps ? Caps.get(secondsString, secondsColor) : Component.text(secondsString).color(secondsColor));

			return finalComponent;
		}

		private Component getLongFormat(final long days, final long hours, final long minutes, final long seconds) {
			Component finalComponent = Component.empty();

			if (this.showDays && days > 0)
				finalComponent = finalComponent
						.append(this.caps ? Caps.get(days + " day" + (days > 1 ? "s" : ""), this.daysColor) : Component.text(days + " day" + (days > 1 ? "s" : "")).color(this.daysColor))
						.append(Component.text(" "));

			if (this.showHours && hours > 0)
				finalComponent = finalComponent
						.append(this.caps ? Caps.get(hours + " hour" + (hours > 1 ? "s" : ""), this.hoursColor) : Component.text(hours + " hour" + (hours > 1 ? "s" : "")).color(this.hoursColor))
						.append(Component.text(" "));

			if (this.showMinutes && minutes > 0)
				finalComponent = finalComponent
						.append(this.caps ? Caps.get(minutes + " minute" + (minutes > 1 ? "s" : ""), this.minutesColor) : Component.text(minutes + " minute" + (minutes > 1 ? "s" : "")).color(this.minutesColor))
						.append(Component.text(" "));

			if (this.showSeconds && seconds > 0)
				finalComponent = finalComponent
						.append(this.caps ? Caps.get(seconds + " second" + (seconds > 1 ? "s" : ""), this.secondsColor) : Component.text(seconds + " second" + (seconds > 1 ? "s" : "")).color(this.secondsColor));

			return finalComponent;
		}

		private Component getShortFormat(final long days, final long hours, final long minutes, final long seconds) {
			Component finalComponent = Component.empty();

			if (this.showDays && days > 0)
				finalComponent = finalComponent
						.append(this.caps ? Caps.get(days + "d", this.daysColor) : Component.text(days + "d").color(this.daysColor))
						.append(Component.text(" "));

			if (this.showHours && hours > 0)
				finalComponent = finalComponent
						.append(this.caps ? Caps.get(hours + "h", this.hoursColor) : Component.text(hours + "h").color(this.hoursColor))
						.append(Component.text(" "));

			if (this.showMinutes && minutes > 0)
				finalComponent = finalComponent
						.append(this.caps ? Caps.get(minutes + "m", this.minutesColor) : Component.text(minutes + "m").color(this.minutesColor))
						.append(Component.text(" "));

			if (this.showSeconds && seconds > 0)
				finalComponent = finalComponent
						.append(this.caps ? Caps.get(seconds + "s", this.secondsColor) : Component.text(seconds + "s").color(this.secondsColor));

			return finalComponent;
		}
	}

	public static class Healthbar {
		private int size = 50;

		private final double maxHealth;
		private final double currentHealth;
		private Type type = Type.PASSIVE;

		private boolean showTotal = false;
		private boolean multiLine = false;

		@Getter
		public enum Type {
			NONE(null, null),
			PASSIVE(Icons.HEALTHBAR_PASSIVE_EMPTY.get(), Icons.HEALTHBAR_PASSIVE_FULL.get()),
			HOSTILE(Icons.HEALTHBAR_HOSTILE_EMPTY.get(), Icons.HEALTHBAR_HOSTILE_FULL.get()),
			BOSS(Icons.HEALTHBAR_BOSS_EMPTY.get(), Icons.HEALTHBAR_BOSS_FULL.get());

			private final Component empty;
			private final Component full;

			Type(final Component empty, final Component full) {
				this.empty = empty;
				this.full = full;
			}

			public static Type get(final String type) {
				try {
					return Type.valueOf(type.toUpperCase());
				} catch (final IllegalArgumentException e) {
					return null;
				}
			}
		}

		public Healthbar(final double maxHealth, final double currentHealth) {
			this.maxHealth = maxHealth;
			this.currentHealth = currentHealth;
		}

		public Healthbar withSize(final int size) {
			this.size = size;
			return this;
		}

		public Healthbar withType(final Type type) {
			this.type = type;
			return this;
		}

		public Healthbar showTotal(final boolean showTotal) {
			this.showTotal = showTotal;
			return this;
		}

		public Healthbar setMultiline(final boolean multiLine) {
			this.multiLine = multiLine;
			return this;
		}

		public Component build() {
			final int fullBars = (int) Math.ceil(this.currentHealth / this.maxHealth * this.size);
			final int emptyBars = this.size - fullBars;

			// Can't use the Spaces Util classes since it uses translations which are a bit buggy when clients reload since they're not reloaded by default
			final Component negativeSpace = Component.text("󏿿").font(new NamespacedKey("minesofmystery", "spaces"));

			final Component border = Icons.HEALTHBAR_BORDER.get();
			final Component fullBar = this.type.getFull();
			final Component emptyBar = this.type.getEmpty();
			Component healthBar = Component.empty();

			if (this.showTotal) {
				healthBar = healthBar
						.append(Caps.get("[", Color.GRAY)
								.append(Caps.get((int) Math.min(this.currentHealth, this.maxHealth), Color.GREEN))
								.append(Caps.get("/", Color.GRAY))
								.append(Caps.get(String.format("%.0f", this.maxHealth), Color.RED))
								.append(Caps.get("]", Color.GRAY)));

				if (this.multiLine)
					healthBar = healthBar.append(Component.newline());
				else
					healthBar = healthBar.append(Component.space());
			}

			// Add left border
			healthBar = healthBar.append(border.append(negativeSpace));

			// Add full and empty bars
			for (int i = 0; i < fullBars; i++)
				healthBar = healthBar.append(fullBar).append(negativeSpace);
			for (int i = 0; i < emptyBars; i++)
				healthBar = healthBar.append(emptyBar).append(negativeSpace);

			// Add right border
			healthBar = healthBar.append(border);

			return healthBar;
		}
	}

	public static class Manabar {
		private int size = 50;

		private final double maxMana;
		private final double currentMana;

		private Key customFont;

		public Manabar(final double maxMana, final double currentMana) {
			this.maxMana = maxMana;
			this.currentMana = currentMana;
		}

		public Manabar withSize(final int size) {
			this.size = size;
			return this;
		}

		public Manabar withFont(final Key font) {
			this.customFont = font;
			return this;
		}

		public Component build() {
			final int fullBars = (int) Math.ceil(this.currentMana / this.maxMana * this.size);
			final int emptyBars = this.size - fullBars;

			// Can't use the Spaces Util classes since it uses translations which are a bit buggy when clients reload since they're not reloaded by default
			final Component negativeSpace = Component.text("󏿿").font(new NamespacedKey("minesofmystery", "spaces"));

			Component borderLeft = Icons.MANABAR_BORDER_LEFT.get();
			Component borderRight = Icons.MANABAR_BORDER_RIGHT.get();
			Component fullBar = Icons.MANABAR_FULL.get();
			Component emptyBar = Icons.MANABAR_EMPTY.get();

			if (this.customFont != null) {
				borderLeft = borderLeft.font(this.customFont);
				borderRight = borderRight.font(this.customFont);
				fullBar = fullBar.font(this.customFont);
				emptyBar = emptyBar.font(this.customFont);
			}

			Component manaBar = Component.empty();

			// Add left border
			manaBar = manaBar.append(borderLeft.append(negativeSpace));

			// Add full and empty bars
			for (int i = 0; i < fullBars; i++)
				manaBar = manaBar.append(fullBar).append(negativeSpace);
			for (int i = 0; i < emptyBars; i++)
				manaBar = manaBar.append(emptyBar).append(negativeSpace);

			// Add right border
			manaBar = manaBar.append(borderRight);

			return manaBar;
		}
	}
}
