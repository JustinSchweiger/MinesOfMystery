package eu.playsc.minesofmystery.dungeon.loot;

import eu.playsc.minesofmystery.custom.CustomItems;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class DungeonLoot {
	private static final double BASE_CHANCE_T1 = 0.03;
	private static final double BASE_CHANCE_T2 = 0.02;
	private static final double BASE_CHANCE_T3 = 0.01;

	public @NotNull Collection<ItemStack> populateEntityLoot(final Random random, @NotNull final DungeonLootContext context) {
		final List<ItemStack> items = new ArrayList<>();
		final int difficulty = context.getDungeonDifficulty();

		if (this.shouldDropItem(random, BASE_CHANCE_T1, difficulty)) {
			items.add(CustomItems.POTION_T1.getItemStack());
		}
		if (this.shouldDropItem(random, BASE_CHANCE_T2, difficulty)) {
			items.add(CustomItems.POTION_T2.getItemStack());
		}
		if (this.shouldDropItem(random, BASE_CHANCE_T3, difficulty)) {
			items.add(CustomItems.POTION_T3.getItemStack());
		}

		return items;
	}

	public @NotNull Collection<ItemStack> populateChestLoot(final Random random, @NotNull final DungeonLootContext context) {
		// ToDo: Implement proper chest loot
		return this.populateEntityLoot(random, context);
	}

	private boolean shouldDropItem(final Random random, final double baseChance, final int difficulty) {
		final double adjustedChance = baseChance * difficulty;
		return random.nextDouble() < adjustedChance;
	}
}
