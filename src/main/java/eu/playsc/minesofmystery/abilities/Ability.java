package eu.playsc.minesofmystery.abilities;

import eu.playsc.minesofmystery.annotations.AutoRegister;
import eu.playsc.minesofmystery.custom.CustomSounds;
import eu.playsc.minesofmystery.model.PlayerData;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import java.time.LocalDateTime;

@Getter
public abstract class Ability {
	private LocalDateTime lastUsed;
	private final PlayerData playerData;

	public Ability(final PlayerData playerData) {
		this.playerData = playerData;
	}

	public void trigger() {
		if (!this.playerData.isEnderDragonDefeated())
			return;

		if (this.lastUsed != null && LocalDateTime.now().isBefore(this.lastUsed.plusSeconds(this.getCooldown()))) {
			CustomSounds.ERROR.play(this.playerData.getPlayer());
			return;
		}

		final boolean isCreative = this.playerData.getPlayer().getGameMode() == GameMode.CREATIVE;

		if (!isCreative && this.playerData.getCurrentMana() < this.getManaCost()) {
			CustomSounds.ERROR.play(this.playerData.getPlayer());
			return;
		}

		this.use(this.playerData.getPlayer());

		if (this instanceof final Cooldownable cooldownable && !isCreative)
			this.playerData.getPlayer().setCooldown(cooldownable.getMaterial(), cooldownable.getCooldown() * 20);

		if (!isCreative) {
			this.playerData.setCurrentMana(this.playerData.getCurrentMana() - this.getManaCost());
			this.lastUsed = LocalDateTime.now();
		}
	}

	public abstract int getManaCost();

	public abstract int getCooldown();

	public abstract Component getName();

	public abstract Component getDescription();

	public abstract NamespacedKey getKey();

	public abstract void use(Player player);

	public static void triggerAbility(final NamespacedKey ability, final Player player) {
		final PlayerData playerData = PlayerData.from(player);
		if (playerData == null || !playerData.isEnderDragonDefeated()) {
			return;
		}

		final Ability abilityInstance = playerData.getAbilities().get(ability);
		if (abilityInstance == null)
			return;

		abilityInstance.trigger();
	}

	@AutoRegister
	public static class Listener implements org.bukkit.event.Listener {
		@EventHandler
		public static void onDashCast(final PlayerSwapHandItemsEvent event) {
			event.setCancelled(true);
			Ability.triggerAbility(DashAbility.KEY, event.getPlayer());
		}
	}
}
