package eu.playsc.minesofmystery.model;

import eu.playsc.minesofmystery.abilities.Ability;
import eu.playsc.minesofmystery.abilities.DashAbility;
import eu.playsc.minesofmystery.abilities.EarthquakeAbility;
import eu.playsc.minesofmystery.abilities.FireballAbility;
import eu.playsc.minesofmystery.common.concurrency.Concurrency;
import eu.playsc.minesofmystery.common.concurrency.SimpleTask;
import eu.playsc.minesofmystery.custom.hud.BossBarHud;
import eu.playsc.minesofmystery.custom.hud.Hud;
import eu.playsc.minesofmystery.database.Database;
import eu.playsc.minesofmystery.huds.ManaHud;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.hibernate.Session;
import org.hibernate.annotations.ColumnDefault;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Entity
@Table(name = "player_data")
@Getter
@NoArgsConstructor(force = true)
public class PlayerData {
	@Getter
	private static final ConcurrentHashMap<UUID, PlayerData> players = new ConcurrentHashMap<>();

	@Id
	private final UUID uuid;
	@Setter
	private String username;
	@Setter
	@ColumnDefault("1")
	private int dungeonLevel;
	@Setter
	@ColumnDefault("0")
	private boolean enderDragonDefeated;

	@Transient
	@Setter
	private Player player;

	// ------------------------- Mana -------------------------
	@Transient
	@Setter
	private int currentMana;
	@Transient
	private int maxMana;
	@Transient
	private final int manaPerSecond = 2;
	@Transient
	private SimpleTask manaRegenTask;

	// ----------------------- Abilities -----------------------
	@Transient
	private final ConcurrentHashMap<NamespacedKey, Ability> abilities = new ConcurrentHashMap<>();

	// ------------------------- HUDs --------------------------
	@Transient
	private final LinkedHashSet<Hud> huds = new LinkedHashSet<>();
	@Transient
	private final LinkedHashSet<BossBarHud> objectiveHuds = new LinkedHashSet<>();
	@Transient
	private SimpleTask hudUpdateTask;

	private PlayerData(final Player player) {
		this.uuid = player.getUniqueId();
		this.dungeonLevel = 1;
		this.enderDragonDefeated = false;
		this.username = player.getName();
	}

	private void handleManaRegen() {
		if (this.currentMana >= this.maxMana)
			return;

		this.currentMana += this.manaPerSecond / 2;
		if (this.currentMana > this.maxMana)
			this.currentMana = this.maxMana;
	}

	private void handleHudRender() {
		this.huds.forEach(Hud::render);
		this.objectiveHuds.forEach(BossBarHud::render);
	}

	private void initTransientProperties() {
		this.maxMana = 100;
		this.currentMana = this.maxMana;

		this.abilities.put(FireballAbility.KEY, new FireballAbility(this));
		this.abilities.put(DashAbility.KEY, new DashAbility(this));
		this.abilities.put(EarthquakeAbility.KEY, new EarthquakeAbility(this));

		this.manaRegenTask = Concurrency.runTimerAsync(10, this::handleManaRegen);
		this.hudUpdateTask = Concurrency.runTimerAsync(2, this::handleHudRender);

		this.registerHud(new ManaHud(this.player));
	}

	public static PlayerData from(final Player player) {
		return players.get(player.getUniqueId());
	}

	public static void createOrUpdate(final Player player) {
		PlayerData playerData = Database.getSession().get(PlayerData.class, player.getUniqueId());
		if (playerData == null)
			playerData = new PlayerData(player);

		playerData.setPlayer(player);
		playerData.setUsername(player.getName());
		playerData.save();

		playerData.initTransientProperties();

		players.put(player.getUniqueId(), playerData);
	}

	public static void remove(@NotNull final Player player) {
		final PlayerData playerData = players.get(player.getUniqueId());
		if (playerData == null)
			return;

		playerData.manaRegenTask.cancel();
		playerData.hudUpdateTask.cancel();
		playerData.save();
		players.remove(player.getUniqueId());
	}

	public void save() {
		players.put(this.uuid, this);

		try (final Session session = Database.getSession()) {
			session.beginTransaction();
			session.merge(this);
			session.getTransaction().commit();
		}
	}

	public void registerHud(final Hud hud) {
		this.huds.add(hud);

		if (hud instanceof final BossBarHud bossBarHud)
			bossBarHud.show();
	}

	public void registerObjectiveHuds(final BossBarHud... huds) {
		this.unregisterObjectiveHuds();

		for (final BossBarHud hud : huds) {
			this.objectiveHuds.add(hud);
			hud.show();
		}
	}

	public void unregisterObjectiveHuds() {
		this.objectiveHuds.forEach(BossBarHud::hide);
		this.objectiveHuds.clear();
	}
}
