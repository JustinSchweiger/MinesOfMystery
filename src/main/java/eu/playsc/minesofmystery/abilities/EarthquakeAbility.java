package eu.playsc.minesofmystery.abilities;

import eu.playsc.minesofmystery.MinesOfMystery;
import eu.playsc.minesofmystery.common.Color;
import eu.playsc.minesofmystery.common.concurrency.Concurrency;
import eu.playsc.minesofmystery.model.PlayerData;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class EarthquakeAbility extends Ability implements Cooldownable {
	public static final NamespacedKey KEY = new NamespacedKey("minesofmystery", "earthquake");
	public static final int MANA_COST = 30;
	public static final int COOLDOWN = 5;
	public static final Material MATERIAL = Material.BROWN_DYE;

	public EarthquakeAbility(final PlayerData playerData) {
		super(playerData);
	}

	@Override
	public int getManaCost() {
		return MANA_COST;
	}

	@Override
	public int getCooldown() {
		return COOLDOWN;
	}

	@Override
	public Material getMaterial() {
		return MATERIAL;
	}

	@Override
	public Component getName() {
		return Component.text("Earthquake").color(Color.ABILITY_EARTHQUAKE);
	}

	@Override
	public Component getDescription() {
		return Component.text("Releases a powerful shockwave around you.").color(Color.GRAY);
	}

	@Override
	public NamespacedKey getKey() {
		return KEY;
	}

	@Override
	public void use(final Player player) {
		final double range = 5;
		final double rings = 10;

		final Location location = player.getLocation();
		location.getWorld().playSound(location, Sound.ENTITY_WITHER_BREAK_BLOCK, 0.3f, 1);
		for (int i = 0; i < rings; i++) {
			final double currentRange = Math.max(0.5, i * (range / rings));
			Concurrency.runLater(i, () -> {
				for (int j = 0; j < 360; j += 10) {
					final double radian = Math.toRadians(j);
					final double x = Math.cos(radian) * currentRange;
					final double z = Math.sin(radian) * currentRange;
					final Location ringLocation = location.clone().add(x, 0.3, z);
					MinesOfMystery.getParticleApi().LIST_1_13.DUST
							.color(org.bukkit.Color.fromARGB(Color.ABILITY_EARTHQUAKE.value()), 0.5f)
							.packet(true, ringLocation)
							.sendInRadiusTo(location.getWorld().getPlayers(), 60);
				}

				location.getWorld().getNearbyLivingEntities(location, currentRange, 2, currentRange).stream()
						.filter(entity -> !(entity instanceof Player))
						.forEach(entity -> {
							entity.damage(50, player);
							entity.setVelocity(entity.getLocation().toVector().subtract(location.toVector()).normalize().multiply(0.5));
						});
			});
		}
	}
}
