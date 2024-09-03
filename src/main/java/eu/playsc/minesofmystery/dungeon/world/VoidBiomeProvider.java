package eu.playsc.minesofmystery.dungeon.world;

import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class VoidBiomeProvider extends BiomeProvider {
	private final Biome biome;

	public VoidBiomeProvider(final Biome paramBiome) {
		this.biome = paramBiome;
	}

	@NotNull
	@Override
	public Biome getBiome(@NotNull final WorldInfo worldInfo, final int x, final int y, final int z) {
		return this.biome;
	}

	@NotNull
	@Override
	public List<Biome> getBiomes(@NotNull final WorldInfo worldInfo) {
		return Collections.singletonList(this.biome);
	}
}
