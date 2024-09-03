package eu.playsc.minesofmystery.dungeon;

import eu.playsc.minesofmystery.common.concurrency.Concurrency;
import eu.playsc.minesofmystery.custom.CustomSounds;
import eu.playsc.minesofmystery.dungeon.entity.DungeonEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@AllArgsConstructor
public class DungeonWaveSpawner {
	private final Wave[] waves;
	private final int waveTickInterval;
	private final int waveSpawnRadius;
	private final int maxWaveSize;

	public void spawn(final Location location) {
		final Random random = new Random();
		final List<Wave> allWaves = this.splitWaves(this.waves);

		for (int waveIndex = 0; waveIndex < allWaves.size(); waveIndex++) {
			final Wave wave = allWaves.get(waveIndex);
			final int delay = waveIndex * this.waveTickInterval;

			Concurrency.runLater(delay, () -> {
				for (int i = 0; i < wave.getAmount(random); i++) {
					final Location spawnLocation = this.getSpawnPosition(location, random);
					wave.getEntity().spawn(spawnLocation.add(0, 1, 0));
				}

				CustomSounds.WAVE.play(location, 0.5f, (float) (0.9 + (random.nextDouble() * 0.7)));
			});
		}
	}

	private List<Wave> splitWaves(final Wave[] waves) {
		final List<Wave> result = new ArrayList<>();

		for (final Wave wave : waves) {
			int totalMobs = wave.getAmount();

			while (totalMobs > this.maxWaveSize) {
				result.add(new Wave(wave.getEntity(), this.maxWaveSize, this.maxWaveSize));
				totalMobs -= this.maxWaveSize;
			}

			if (totalMobs > 0) {
				result.add(new Wave(wave.getEntity(), totalMobs, totalMobs));
			}
		}

		return result;
	}

	private Location getSpawnPosition(final Location location, final Random random) {
		Location spawnLocation;
		int attempts = 0;

		do {
			if (attempts > 10) {
				spawnLocation = location;
				break;
			}

			spawnLocation = location.clone().add(
					random.nextInt(this.waveSpawnRadius * 2) - this.waveSpawnRadius,
					random.nextInt(5) - 2,
					random.nextInt(this.waveSpawnRadius * 2) - this.waveSpawnRadius
			);
			attempts++;
		} while (!this.canSpawn(spawnLocation.toCenterLocation()));

		return spawnLocation.toCenterLocation();
	}

	private boolean canSpawn(final Location location) {
		return !location.getBlock().isEmpty() &&
		       location.clone().add(0, 1, 0).getBlock().isEmpty() &&
		       location.clone().add(0, 2, 0).getBlock().isEmpty();
	}

	public static class Builder {
		private Wave[] waves;
		private int waveTickInterval = 60;
		private int waveSpawnRadius = 4;
		private int maxWaveSize = 10;

		public Builder waves(final Wave... waves) {
			this.waves = waves;
			return this;
		}

		public Builder waveTickInterval(final int waveTickInterval) {
			this.waveTickInterval = waveTickInterval;
			return this;
		}

		public Builder waveSpawnRadius(final int waveSpawnRadius) {
			this.waveSpawnRadius = waveSpawnRadius;
			return this;
		}

		public Builder maxWaveSize(final int maxWaveSize) {
			this.maxWaveSize = maxWaveSize;
			return this;
		}

		public DungeonWaveSpawner build() {
			return new DungeonWaveSpawner(this.waves, this.waveTickInterval, this.waveSpawnRadius, this.maxWaveSize);
		}
	}

	@Getter
	public static class Wave {
		private final DungeonEntity entity;
		private final int minAmount;
		private final int maxAmount;
		private final int amount;

		public Wave(final DungeonEntity entity, final int minAmount, final int maxAmount, final Random random) {
			this.entity = entity;
			this.minAmount = minAmount;
			this.maxAmount = maxAmount;
			this.amount = this.getAmount(random);
		}

		public Wave(final DungeonEntity entity, final int minAmount, final int maxAmount) {
			this(entity, minAmount, maxAmount, new Random());
		}

		public Wave(final DungeonEntities entity, final int minAmount, final int maxAmount, final Random random) {
			this(entity.createEntity(), minAmount, maxAmount, random);
		}

		public Wave(final DungeonEntities entity, final int minAmount, final int maxAmount) {
			this(entity.createEntity(), minAmount, maxAmount, new Random());
		}

		private int getAmount(final Random random) {
			if (this.minAmount == this.maxAmount) {
				return this.minAmount;
			}

			return random.nextInt(this.maxAmount - this.minAmount) + this.minAmount;
		}
	}
}
