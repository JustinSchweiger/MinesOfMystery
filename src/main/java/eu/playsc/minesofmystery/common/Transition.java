package eu.playsc.minesofmystery.common;

import eu.playsc.minesofmystery.common.concurrency.Concurrency;
import eu.playsc.minesofmystery.custom.fonts.Icons;
import eu.playsc.minesofmystery.custom.fonts.Spaces;
import eu.playsc.minesofmystery.custom.internal.CustomFont;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@AllArgsConstructor
public class Transition {
	private final Component message;
	private final Time timing;
	private final TextColor backgroundColor;
	private final Type transitionType;
	private final boolean freeze;
	private final List<Player> players;
	private final Runnable run;

	public void show(final List<Player> players) {
		final Component title = this.getTitle();
		final Title.Times times = this.timing.getTitleTime();

		for (final Player player : players) {
			player.showTitle(Title.title(title, Component.empty(), times));

			Concurrency.runLater(this.timing.getFadeIn() + 2, () -> {
				if (this.freeze) {
					player.addPotionEffect(this.getFreezeEffect());
				}

				this.run.run();
			});
		}
	}

	public void show(final Player... player) {
		this.show(List.of(player));
	}

	public void show() {
		this.show(this.players);
	}

	private Component getTitle() {
		final Component left, right;
		if (this.transitionType == Type.FULL) {
			left = Component.text(Icons.TRANSITION_FULL.getCharacter().split("")[0]).color(this.backgroundColor).font(Icons.TRANSITION_FULL.getFontKey());
			right = Component.text(Icons.TRANSITION_FULL.getCharacter().split("")[1]).color(this.backgroundColor).font(Icons.TRANSITION_FULL.getFontKey());
		} else {
			left = Component.text(Icons.TRANSITION_TRANSPARENT.getCharacter().split("")[0]).color(this.backgroundColor).font(Icons.TRANSITION_TRANSPARENT.getFontKey());
			right = Component.text(Icons.TRANSITION_TRANSPARENT.getCharacter().split("")[1]).color(this.backgroundColor).font(Icons.TRANSITION_TRANSPARENT.getFontKey());
		}

		final Component background = left.append(Spaces.negative(1)).append(right);

		if (this.message != null) {
			final CustomFont.CountResult countResult = CustomFont.countCharacters(this.message);
			final int length = countResult.calculateWidth(new HashMap<>());

			return Spaces.negative(256)
					.append(Spaces.positive(length / 2))
					.append(background)
					.append(Spaces.negative(256))
					.append(Spaces.negative(length / 2))
					.append(this.message);
		}

		return background;
	}

	private PotionEffect getFreezeEffect() {
		return new PotionEffect(PotionEffectType.SLOW, this.timing.getStay() - 2, 100, false, false);
	}

	public enum Type {
		FULL,
		TRANSPARENT
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Time {
		private int fadeIn = 20;
		private int stay = 60;
		private int fadeOut = 20;

		protected Title.Times getTitleTime() {
			return Title.Times.times(Ticks.duration(this.fadeIn), Ticks.duration(this.stay), Ticks.duration(this.fadeOut));
		}
	}

	public static class Builder {
		private Component message = null;
		private Time timing = new Time();
		private TextColor backgroundColor = Color.BLACK;
		private Type transitionType = Type.FULL;
		private boolean freeze = false;
		private List<Player> players = new ArrayList<>();
		private Runnable run = () -> {
		};

		public Builder message(final Component message) {
			this.message = message;
			return this;
		}

		public Builder timing(final Time timing) {
			this.timing = timing;
			return this;
		}

		public Builder color(final TextColor backgroundColor) {
			this.backgroundColor = backgroundColor;
			return this;
		}

		public Builder type(final Type transitionType) {
			this.transitionType = transitionType;
			return this;
		}

		public Builder freeze(final boolean freeze) {
			this.freeze = freeze;
			return this;
		}

		public Builder addPlayers(final List<Player> players) {
			this.players = players;
			return this;
		}

		public Builder addPlayers(final Player... player) {
			Collections.addAll(this.players, player);
			return this;
		}

		public Builder addPlayer(final Player player) {
			this.players.add(player);
			return this;
		}

		public Builder run(final Runnable run) {
			this.run = run;
			return this;
		}

		public Transition build() {
			return new Transition(this.message, this.timing, this.backgroundColor, this.transitionType, this.freeze, this.players, this.run);
		}
	}
}
