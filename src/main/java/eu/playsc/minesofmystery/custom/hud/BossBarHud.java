package eu.playsc.minesofmystery.custom.hud;

import lombok.NoArgsConstructor;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

@NoArgsConstructor(force = true)
public abstract class BossBarHud extends Hud {
	private final BossBar bossBar;

	public BossBarHud(final Player player) {
		super(player);
		this.bossBar = BossBar.bossBar(Component.text(this.getKey()), 0, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS);
	}

	public void update(final Component component) {
		this.bossBar.name(component);
	}

	public void show() {
		this.bossBar.addViewer(this.getPlayer());
	}

	public void hide() {
		this.bossBar.removeViewer(this.getPlayer());
	}
}
