package eu.playsc.minesofmystery.dungeon.entity;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import eu.playsc.minesofmystery.annotations.AutoRegister;
import eu.playsc.minesofmystery.common.Color;
import eu.playsc.minesofmystery.common.Formatter;
import eu.playsc.minesofmystery.custom.fonts.Caps;
import eu.playsc.minesofmystery.dungeon.DungeonTeam;
import eu.playsc.minesofmystery.dungeon.handlers.DungeonHandler;
import eu.playsc.minesofmystery.dungeon.loot.DungeonLoot;
import eu.playsc.minesofmystery.dungeon.loot.DungeonLootContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@AllArgsConstructor
public class DungeonEntity {
	public static final NamespacedKey DUNGEON_ENTITY = new NamespacedKey("minesofmystery", "dungeon_entity");
	public static final NamespacedKey DUNGEON_BOSS_ENTITY = new NamespacedKey("minesofmystery", "dungeon_boss_entity");

	public static final NamespacedKey HEALTHBAR = new NamespacedKey("minesofmystery", "healthbar");
	public static final NamespacedKey HEALTHBAR_HEALTH = new NamespacedKey("minesofmystery", "healthbar_health");
	public static final NamespacedKey CUSTOM_NAME = new NamespacedKey("minesofmystery", "custom_name");

	private EntityType type;
	private String name;
	private Formatter.Healthbar.Type healthbarType;
	private boolean showHealthbarHealth;
	private List<Attribute> attributes;
	private Equipment equipment;

	public void spawnBoss(final Location location) {
		location.getWorld().spawnEntity(location, this.type, CreatureSpawnEvent.SpawnReason.CUSTOM, e -> {
			final LivingEntity entity = (LivingEntity) e;
			entity.getPersistentDataContainer().set(DUNGEON_BOSS_ENTITY, PersistentDataType.BOOLEAN, true);

			this.actuallySpawnEntity(location, entity);
		});
	}

	private void actuallySpawnEntity(final Location location, final LivingEntity entity) {
		entity.setPersistent(true);
		entity.setRemoveWhenFarAway(false);
		entity.setSilent(true);
		entity.setCanPickupItems(false);
		this.handleEquipment(entity);

		if (this.type == EntityType.ZOMBIE) {
			final Zombie zombie = (Zombie) entity;
			zombie.setBaby(false);
			zombie.setShouldBurnInDay(false);
		}

		if (this.type == EntityType.SKELETON) {
			final Skeleton skeleton = (Skeleton) entity;
			skeleton.setShouldBurnInDay(false);
		}

		entity.getPersistentDataContainer().set(DUNGEON_ENTITY, PersistentDataType.BOOLEAN, true);


		if (!this.name.isEmpty()) {
			entity.setCustomNameVisible(false);
			entity.getPersistentDataContainer().set(CUSTOM_NAME, PersistentDataType.STRING, this.name);
		}

		if (this.healthbarType != Formatter.Healthbar.Type.NONE) {
			entity.getPersistentDataContainer().set(HEALTHBAR, PersistentDataType.STRING, this.healthbarType.name());
			entity.getPersistentDataContainer().set(HEALTHBAR_HEALTH, PersistentDataType.BOOLEAN, this.showHealthbarHealth);
		}

		DungeonHandler.get().getDungeon(location.getWorld()).ifPresentOrElse(
				(dungeonWorld) -> this.addAttributes(entity, dungeonWorld.getDifficulty()),
				() -> this.addAttributes(entity, 1)
		);

		entity.setHealth(entity.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue());
	}

	public void spawn(final Location location) {
		location.getWorld().spawnEntity(location, this.type, CreatureSpawnEvent.SpawnReason.CUSTOM, e -> {
			final LivingEntity entity = (LivingEntity) e;

			this.actuallySpawnEntity(location, entity);
		});
	}

	private void addAttributes(final LivingEntity entity, final double difficulty) {
		this.attributes.forEach(attribute -> {
			final double value = this.getAttributeValue(attribute, difficulty);
			entity.getAttribute(attribute.getAttribute()).setBaseValue(value);
		});
	}

	private double getAttributeValue(final Attribute attribute, final double difficulty) {
		return attribute.isScalesWithDifficulty() ? attribute.getBaseValue() + (attribute.getDifficultyScalingMultiplier() * difficulty) : attribute.getBaseValue();
	}

	private void handleEquipment(final LivingEntity entity) {
		final EntityEquipment equipment = entity.getEquipment();
		if (equipment == null)
			return;

		equipment.clear();
		equipment.setDropChance(EquipmentSlot.HEAD, 0);
		equipment.setDropChance(EquipmentSlot.CHEST, 0);
		equipment.setDropChance(EquipmentSlot.LEGS, 0);
		equipment.setDropChance(EquipmentSlot.FEET, 0);
		equipment.setDropChance(EquipmentSlot.HAND, 0);
		equipment.setDropChance(EquipmentSlot.OFF_HAND, 0);

		if (this.equipment != null) {
			if (this.equipment.getHelmet() != null)
				equipment.setHelmet(this.equipment.getHelmet());

			if (this.equipment.getChestplate() != null)
				equipment.setChestplate(this.equipment.getChestplate());

			if (this.equipment.getLeggings() != null)
				equipment.setLeggings(this.equipment.getLeggings());

			if (this.equipment.getBoots() != null)
				equipment.setBoots(this.equipment.getBoots());

			if (this.equipment.getItemInMainHand() != null)
				equipment.setItemInMainHand(this.equipment.getItemInMainHand());

			if (this.equipment.getItemInOffHand() != null)
				equipment.setItemInOffHand(this.equipment.getItemInOffHand());
		}
	}

	@Getter
	public static class Attribute {
		private final org.bukkit.attribute.Attribute attribute;
		private final double baseValue;
		private final boolean scalesWithDifficulty;
		private final double difficultyScalingMultiplier;

		public Attribute(final org.bukkit.attribute.Attribute attribute, final double baseValue, final boolean scalesWithDifficulty, final double difficultyScalingMultiplier) {
			this.attribute = attribute;
			this.baseValue = baseValue;
			this.scalesWithDifficulty = scalesWithDifficulty;
			this.difficultyScalingMultiplier = difficultyScalingMultiplier;
		}

		public Attribute(final org.bukkit.attribute.Attribute attribute, final double baseValue, final boolean scalesWithDifficulty) {
			this(attribute, baseValue, scalesWithDifficulty, 1);
		}
	}

	@Getter
	@AllArgsConstructor
	public static class Equipment {
		private final ItemStack helmet;
		private final ItemStack chestplate;
		private final ItemStack leggings;
		private final ItemStack boots;
		private final ItemStack itemInMainHand;
		private final ItemStack itemInOffHand;

		public static class Builder {
			private ItemStack helmet;
			private ItemStack chestplate;
			private ItemStack leggings;
			private ItemStack boots;
			private ItemStack itemInMainHand;
			private ItemStack itemInOffHand;

			public Builder helmet(final ItemStack helmet) {
				this.helmet = helmet;
				return this;
			}

			public Builder chestplate(final ItemStack chestplate) {
				this.chestplate = chestplate;
				return this;
			}

			public Builder leggings(final ItemStack leggings) {
				this.leggings = leggings;
				return this;
			}

			public Builder boots(final ItemStack boots) {
				this.boots = boots;
				return this;
			}

			public Builder itemInMainHand(final ItemStack itemInMainHand) {
				this.itemInMainHand = itemInMainHand;
				return this;
			}

			public Builder itemInOffHand(final ItemStack itemInOffHand) {
				this.itemInOffHand = itemInOffHand;
				return this;
			}

			public Equipment build() {
				return new Equipment(this.helmet, this.chestplate, this.leggings, this.boots, this.itemInMainHand, this.itemInOffHand);
			}
		}
	}

	public static class Builder {
		private EntityType type = EntityType.ZOMBIE;
		private String name = "";
		private Formatter.Healthbar.Type healthbarType = Formatter.Healthbar.Type.HOSTILE;
		private boolean showHealthbarHealth = true;
		private final List<Attribute> attributes = new ArrayList<>(List.of(
				new Attribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH, 20, true),
				new Attribute(org.bukkit.attribute.Attribute.GENERIC_MOVEMENT_SPEED, 0.23, false),
				new Attribute(org.bukkit.attribute.Attribute.GENERIC_ATTACK_DAMAGE, 5, false)
		));
		private Equipment equipment;

		public Builder setType(final EntityType type) {
			this.type = type;
			return this;
		}

		public Builder setName(final String name) {
			this.name = name;
			return this;
		}

		public Builder setHealthbarType(final Formatter.Healthbar.Type healthbarType) {
			this.healthbarType = healthbarType;
			return this;
		}

		public Builder setShowHealthbarHealth(final boolean showHealthbarHealth) {
			this.showHealthbarHealth = showHealthbarHealth;
			return this;
		}

		public Builder addAttribute(final Attribute attribute) {
			this.attributes.add(attribute);
			return this;
		}

		public Builder addAttributes(final List<Attribute> attributes) {
			this.attributes.addAll(attributes);
			return this;
		}

		public Builder setAttributes(final List<Attribute> attributes) {
			this.attributes.clear();
			this.attributes.addAll(attributes);
			return this;
		}

		public Builder setEquipment(final Equipment equipment) {
			this.equipment = equipment;
			return this;
		}

		public DungeonEntity build() {
			return new DungeonEntity(
					this.type,
					this.name,
					this.healthbarType,
					this.showHealthbarHealth,
					this.attributes,
					this.equipment
			);
		}
	}

	@AutoRegister
	public static class Listener implements org.bukkit.event.Listener {
		@EventHandler
		public static void onEntitySpawn(final CreatureSpawnEvent event) {
			final LivingEntity entity = event.getEntity();
			if (!entity.getPersistentDataContainer().has(DUNGEON_ENTITY))
				return;

			final Component component = handleTextDisplays(entity, entity.getHealth());

			final TextDisplay textDisplay = (TextDisplay) event.getLocation().getWorld().spawnEntity(event.getLocation(), EntityType.TEXT_DISPLAY);
			textDisplay.setPersistent(true);
			textDisplay.setViewRange(10);
			textDisplay.setBillboard(Display.Billboard.CENTER);
			textDisplay.setTextOpacity((byte) 255);
			textDisplay.setBackgroundColor(org.bukkit.Color.fromARGB(0, 0, 0, 0));
			textDisplay.setShadowed(false);
			textDisplay.text(component);

			textDisplay.setTransformation(new Transformation(
					new Vector3f(0, 0.1f, 0),
					new AxisAngle4f(),
					new Vector3f(1, 1, 1),
					new AxisAngle4f()
			));

			entity.addPassenger(textDisplay);
		}

		@EventHandler
		public static void onEntityDamage(final EntityDamageEvent event) {
			if (!(event.getEntity() instanceof final LivingEntity entity) || !entity.getPersistentDataContainer().has(DUNGEON_ENTITY))
				return;

			if (!event.getEntity().getPersistentDataContainer().has(HEALTHBAR) || entity.getPassengers().isEmpty())
				return;

			final TextDisplay textDisplay = (TextDisplay) entity.getPassengers().get(0);
			textDisplay.text(handleTextDisplays(entity, entity.getHealth() - event.getFinalDamage() + 1));
		}

		@EventHandler
		public static void onEntityDeath(final EntityDeathEvent event) {
			if (!event.getEntity().getPersistentDataContainer().has(DUNGEON_ENTITY))
				return;

			event.setDroppedExp(0);
			event.getDrops().clear();
			if (event.getEntity().getKiller() != null && DungeonTeam.get(event.getEntity().getKiller()).getDungeon() != null) {
				final DungeonLootContext context = new DungeonLootContext(
						event.getEntity().getLocation(),
						event.getEntity(),
						event.getEntity().getKiller()
				);

				event.getDrops().addAll(new DungeonLoot().populateEntityLoot(new Random(), context));
			}

			if ((
					    !event.getEntity().getPersistentDataContainer().has(CUSTOM_NAME) &&
					    !event.getEntity().getPersistentDataContainer().has(HEALTHBAR)
			    ) ||
			    event.getEntity().getPassengers().isEmpty())
				return;

			event.getEntity().getPassengers().get(0).remove();
		}

		@EventHandler
		public static void onDespawn(final EntityRemoveFromWorldEvent event) {
			if (!(event.getEntity() instanceof final LivingEntity entity) || entity.getPassengers().isEmpty())
				return;

			if (!entity.getPersistentDataContainer().has(DUNGEON_ENTITY))
				return;

			if ((
					    !event.getEntity().getPersistentDataContainer().has(CUSTOM_NAME) &&
					    !event.getEntity().getPersistentDataContainer().has(HEALTHBAR)
			    ) ||
			    event.getEntity().getPassengers().isEmpty())
				return;

			entity.getPassengers().get(0).remove();
		}

		private static Component handleTextDisplays(final LivingEntity entity, final double health) {
			Component component = Component.empty();

			// Add Healthbar
			if (entity.getPersistentDataContainer().has(HEALTHBAR, PersistentDataType.STRING)) {
				component = component.append(getHealthComponent(
						entity.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue(),
						Math.max(0, health),
						entity.getPersistentDataContainer().get(HEALTHBAR, PersistentDataType.STRING),
						entity.getPersistentDataContainer().get(HEALTHBAR_HEALTH, PersistentDataType.BOOLEAN)
				));
			}

			component = component.append(Component.newline());

			// Add Custom Name
			if (entity.getPersistentDataContainer().has(CUSTOM_NAME, PersistentDataType.STRING)) {
				component = component.append(Caps.get(entity.getPersistentDataContainer().get(CUSTOM_NAME, PersistentDataType.STRING), Color.GRAY));
			}

			return component;
		}

		private static Component getHealthComponent(final double maxHealth, final double currentHealth, final String type, final boolean showHealth) {
			Formatter.Healthbar.Type healthbarType = Formatter.Healthbar.Type.get(type);
			if (healthbarType == null)
				healthbarType = Formatter.Healthbar.Type.PASSIVE;

			return new Formatter.Healthbar(maxHealth, currentHealth)
					.showTotal(showHealth)
					.setMultiline(true)
					.withType(healthbarType)
					.build();
		}
	}
}