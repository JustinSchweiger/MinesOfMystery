package eu.playsc.minesofmystery.abilities;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import eu.playsc.minesofmystery.MinesOfMystery;
import eu.playsc.minesofmystery.annotations.AutoRegister;
import eu.playsc.minesofmystery.common.Color;
import eu.playsc.minesofmystery.custom.CustomSounds;
import eu.playsc.minesofmystery.model.PlayerData;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftArrow;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collection;
import java.util.UUID;

public class FireballAbility extends Ability implements Cooldownable {
	public static final NamespacedKey KEY = new NamespacedKey("minesofmystery", "fireball");
	public static final int MANA_COST = 10;
	public static final int COOLDOWN = 1;
	public static final Material MATERIAL = Material.ORANGE_DYE;

	private static final NamespacedKey FIREBALL_ENTITY = new NamespacedKey("minesofmystery", "fireball_entity");
	private static final NamespacedKey FIREBALL_SHOOTER = new NamespacedKey("minesofmystery", "fireball_shooter");

	public FireballAbility(final PlayerData playerData) {
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
		return Component.text("Fireball").color(Color.ABILITY_FIREBALL);
	}

	@Override
	public Component getDescription() {
		return Component.text("Shoot a fireball in the direction you are looking!").color(Color.GRAY);
	}

	@Override
	public NamespacedKey getKey() {
		return KEY;
	}

	@Override
	public void use(final Player player) {
		final Location location = player.getLocation();

		CustomSounds.FIREBALL_SHOOT.play(location, 0.2f, 1f);
		final Arrow arrow = player.launchProjectile(Arrow.class, location.getDirection().multiply(1.2), arrowEntity -> {
			arrowEntity.setSilent(true);
			arrowEntity.setGravity(false);
			arrowEntity.getPersistentDataContainer().set(FIREBALL_ENTITY, PersistentDataType.BOOLEAN, true);
			arrowEntity.getPersistentDataContainer().set(FIREBALL_SHOOTER, PersistentDataType.STRING, player.getUniqueId().toString());
		});

		arrow.setVelocity(location.getDirection().multiply(2));
		for (final Player onlinePlayer : player.getWorld().getPlayers()) {
			((CraftPlayer) onlinePlayer).getHandle().connection.send(new ClientboundRemoveEntitiesPacket(((CraftArrow) arrow).getHandle().getId()));
		}
	}

	@AutoRegister
	public static class Listener implements org.bukkit.event.Listener {
		@EventHandler
		public static void onImpact(final ProjectileHitEvent event) {
			if (!event.getEntity().getPersistentDataContainer().has(FIREBALL_ENTITY, PersistentDataType.BOOLEAN)) {
				return;
			}

			final Arrow arrow = (Arrow) event.getEntity();
			final Location location = arrow.getLocation();
			final Collection<LivingEntity> nearbyEntities = arrow.getWorld().getNearbyLivingEntities(location, 3);
			final Player shooter = Bukkit.getPlayer(UUID.fromString(arrow.getPersistentDataContainer().get(FIREBALL_SHOOTER, PersistentDataType.STRING)));

			event.setCancelled(true);
			arrow.remove();
			CustomSounds.FIREBALL_IMPACT.play(location.getNearbyPlayers(60), location, 1, 1);

			MinesOfMystery.getParticleApi().LIST_1_13.FLAME
					.packet(true, location.getX(), location.getY() + 0.2, location.getZ(), 2, 2, 2, 0.01, 1000)
					.sendInRadiusTo(arrow.getWorld().getPlayers(), 60);
			MinesOfMystery.getParticleApi().LIST_1_13.EXPLOSION
					.packet(true, location.getX(), location.getY() + 0.2, location.getZ(), 1, 1, 1, 0.01, 3)
					.sendInRadiusTo(arrow.getWorld().getPlayers(), 60);
			nearbyEntities
					.stream()
					.filter(entity -> !(entity instanceof Player))
					.forEach(entity -> {
						if (shooter != null)
							entity.damage(20, shooter);
						else
							entity.damage(20);
						entity.setFireTicks(40);
					});
		}

		@EventHandler
		public static void onTick(final ServerTickEndEvent event) {
			// Get all arrows in the world
			Bukkit.getServer().getWorlds().forEach(world -> world.getEntitiesByClass(Arrow.class).stream()
					.filter(arrow -> arrow.getPersistentDataContainer().has(FIREBALL_ENTITY, PersistentDataType.BOOLEAN))
					.forEach(arrow -> {
						final Location location = arrow.getLocation();
						MinesOfMystery.getParticleApi().LIST_1_13.CLOUD
								.packet(true, location.getX(), location.getY(), location.getZ(), 0.01, 0.01, 0.01, 0.01, 1)
								.sendInRadiusTo(arrow.getWorld().getPlayers(), 60);
					}));
		}
	}
}
