package eu.playsc.minesofmystery.dungeon;

import eu.playsc.minesofmystery.common.Formatter;
import eu.playsc.minesofmystery.dungeon.entity.DungeonEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Getter
@AllArgsConstructor
public enum DungeonEntities {
	BOSS_T1(
			EntityType.EVOKER,
			"Dungeon Boss",
			Formatter.Healthbar.Type.BOSS,
			true,
			List.of(
					new DungeonEntity.Attribute(Attribute.GENERIC_MAX_HEALTH, 500, true, 1.5),
					new DungeonEntity.Attribute(Attribute.GENERIC_MOVEMENT_SPEED, 0.27, false),
					new DungeonEntity.Attribute(Attribute.GENERIC_ATTACK_DAMAGE, 10, false),
					new DungeonEntity.Attribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE, 255, false),
					new DungeonEntity.Attribute(Attribute.GENERIC_FOLLOW_RANGE, 50, false)
			),
			null
	),

	ZOMBIE_T1(
			EntityType.ZOMBIE,
			"",
			Formatter.Healthbar.Type.HOSTILE,
			false,
			List.of(
					new DungeonEntity.Attribute(Attribute.GENERIC_MAX_HEALTH, 20, false),
					new DungeonEntity.Attribute(Attribute.GENERIC_MOVEMENT_SPEED, 0.23, false),
					new DungeonEntity.Attribute(Attribute.GENERIC_ATTACK_DAMAGE, 5, false),
					new DungeonEntity.Attribute(Attribute.GENERIC_FOLLOW_RANGE, 50, false)
			),
			null
	),
	ZOMBIE_T2(
			EntityType.ZOMBIE,
			"",
			Formatter.Healthbar.Type.HOSTILE,
			false,
			List.of(
					new DungeonEntity.Attribute(Attribute.GENERIC_MAX_HEALTH, 20, true, 1.3),
					new DungeonEntity.Attribute(Attribute.GENERIC_MOVEMENT_SPEED, 0.24, false),
					new DungeonEntity.Attribute(Attribute.GENERIC_ATTACK_DAMAGE, 7, false),
					new DungeonEntity.Attribute(Attribute.GENERIC_FOLLOW_RANGE, 50, false)
			),
			new DungeonEntity.Equipment.Builder().itemInMainHand(new ItemStack(Material.STONE_SWORD)).build()
	),
	ZOMBIE_T3(
			EntityType.ZOMBIE,
			"",
			Formatter.Healthbar.Type.HOSTILE,
			true,
			List.of(
					new DungeonEntity.Attribute(Attribute.GENERIC_MAX_HEALTH, 150, true, 1.5),
					new DungeonEntity.Attribute(Attribute.GENERIC_MOVEMENT_SPEED, 0.20, false),
					new DungeonEntity.Attribute(Attribute.GENERIC_ATTACK_DAMAGE, 3, false),
					new DungeonEntity.Attribute(Attribute.GENERIC_FOLLOW_RANGE, 50, false)
			),
			null
	);

	private final EntityType entityType;
	private final String name;
	private final Formatter.Healthbar.Type healthbarType;
	private final boolean showHealthNumbers;
	private final List<DungeonEntity.Attribute> attributes;
	private final DungeonEntity.Equipment equipment;

	public DungeonEntity createEntity() {
		return new DungeonEntity.Builder()
				.setType(this.entityType)
				.setName(this.name)
				.setHealthbarType(this.healthbarType)
				.setShowHealthbarHealth(this.showHealthNumbers)
				.setAttributes(this.attributes)
				.setEquipment(this.equipment)
				.build();
	}
}
