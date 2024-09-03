package eu.playsc.minesofmystery.custom.hud;

import eu.playsc.minesofmystery.annotations.AutoRegister;
import eu.playsc.minesofmystery.common.concurrency.Concurrency;
import eu.playsc.minesofmystery.common.concurrency.SimpleTask;
import eu.playsc.minesofmystery.custom.fonts.Spaces;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.LinkedHashMap;

@NoArgsConstructor(force = true)
public abstract class ActionBarHud extends Hud {
	private static final LinkedHashMap<Player, LinkedHashMap<String, ActionBarHud>> ACTION_BAR_HUDS = new LinkedHashMap<>();
	private static SimpleTask UPDATE_TASK;

	private Component component;
	private int offset;

	public ActionBarHud(final Player player) {
		super(player);
		if (!ACTION_BAR_HUDS.containsKey(player))
			ACTION_BAR_HUDS.put(player, new LinkedHashMap<>());

		ACTION_BAR_HUDS.get(player).put(this.getKey(), this);

		if (UPDATE_TASK != null)
			return;

		UPDATE_TASK = Concurrency.runTimerAsync(2, ActionBarHud::updateAll);
	}

	public void update(final Component component, final int offset) {
		this.component = component;
		this.offset = offset;
	}

	private static void updateAll() {
		if (ACTION_BAR_HUDS.isEmpty())
			return;

		ACTION_BAR_HUDS.forEach((player, huds) -> {
			if (player.getGameMode().equals(GameMode.SPECTATOR)) {
				player.sendActionBar(Component.empty());
				return;
			}

			Component mergedComponent = Component.empty();
			for (final ActionBarHud hud : huds.values()) {
				if (hud.component == null)
					continue;

				if (hud.getXOffset() == 0) {
					mergedComponent = mergedComponent.append(hud.component).append(Spaces.negative(hud.offset));
				} else if (hud.getXOffset() > 0) {
					mergedComponent = mergedComponent
							.append(Spaces.positive(hud.getXOffset()))
							.append(hud.component)
							.append(Spaces.negative(hud.getXOffset()))
							.append(Spaces.negative(hud.offset));
				} else {
					mergedComponent = mergedComponent
							.append(Spaces.negative(Math.abs(hud.getXOffset())))
							.append(hud.component)
							.append(Spaces.positive(Math.abs(hud.getXOffset())))
							.append(Spaces.negative(hud.offset));
				}
			}

			player.sendActionBar(mergedComponent);
		});
	}

	@AutoRegister
	public static class Listener implements org.bukkit.event.Listener {
		@EventHandler
		public static void onPlayerLeave(final PlayerQuitEvent event) {
			ACTION_BAR_HUDS.remove(event.getPlayer());
		}
	}
}
