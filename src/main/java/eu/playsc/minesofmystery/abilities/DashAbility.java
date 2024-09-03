package eu.playsc.minesofmystery.abilities;

import eu.playsc.minesofmystery.MinesOfMystery;
import eu.playsc.minesofmystery.common.Color;
import eu.playsc.minesofmystery.custom.CustomSounds;
import eu.playsc.minesofmystery.model.PlayerData;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

public class DashAbility extends Ability {
	public static final NamespacedKey KEY = new NamespacedKey("minesofmystery", "dash");

	public DashAbility(final PlayerData playerData) {
		super(playerData);
	}

	@Override
	public int getManaCost() {
		return 20;
	}

	@Override
	public int getCooldown() {
		return 8;
	}

	@Override
	public Component getName() {
		return Component.text("Dash").color(Color.ABILITY_DASH);
	}

	@Override
	public Component getDescription() {
		return Component.text("Dash through the air. The speed is based on your current velocity!").color(Color.GRAY);
	}

	@Override
	public NamespacedKey getKey() {
		return KEY;
	}

	@Override
	public void use(final Player player) {
		final Location location = player.getLocation();

		CustomSounds.DASH.play(player.getLocation(), 0.5f, 1f);
		MinesOfMystery.getParticleApi().LIST_1_13.CLOUD
				.packet(true, location.getBlockX(), location.getY() + 0.2, location.getZ(), 1, 0.2, 1, 0.01, 30)
				.sendInRadiusTo(player, 30);

		player.setVelocity(location.getDirection().multiply(2));
	}
}
