package eu.playsc.minesofmystery.dungeon.world;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.session.ClipboardHolder;
import eu.playsc.minesofmystery.common.Common;
import eu.playsc.minesofmystery.common.concurrency.Concurrency;
import eu.playsc.minesofmystery.dungeon.DungeonRegionType;
import eu.playsc.minesofmystery.dungeon.DungeonTheme;
import eu.playsc.minesofmystery.dungeon.objectives.DungeonObjective;
import eu.playsc.minesofmystery.dungeon.objectives.KillMonstersObjective;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

@NoArgsConstructor
public class DungeonGenerator extends ChunkGenerator {
	private final int maxRooms = 3;
	private static final int STRUCTURE_PLACEMENT_Y = 125;

	private DungeonTheme theme = null;
	private DungeonObjective objective = null;

	public DungeonGenerator(final DungeonTheme theme, final DungeonObjective objective) {
		this.theme = theme;
		this.objective = objective;
	}

	@Override
	public boolean canSpawn(@NotNull final World world, final int x, final int z) {
		return true;
	}

	@Override
	public @Nullable BiomeProvider getDefaultBiomeProvider(@NotNull final WorldInfo worldInfo) {
		return new VoidBiomeProvider(Biome.NETHER_WASTES);
	}

	@Override
	public void generateSurface(@NotNull final WorldInfo worldInfo, @NotNull final Random random, final int chunkX, final int chunkZ, @NotNull final ChunkGenerator.ChunkData chunkData) {
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int y = 120; y < 200; y++) {
					chunkData.setBlock(x, y, z, Material.BEDROCK);
				}
			}
		}
	}

	@Override
	public Location getFixedSpawnLocation(@NotNull final World world, @NotNull final Random random) {
		final Location spawnLocation = new Location(world, 8, 133, 12.5, -180, 0);

		// Generate the dungeon rooms and corridors
		Concurrency.runAsync(() -> this.generateDungeon(world, random));

		return spawnLocation;
	}

	private void generateDungeon(final World world, final Random random) {
		this.placeEntrance(world, random);

		for (int dx = -this.maxRooms; dx <= this.maxRooms; dx++) {
			for (int dz = -this.maxRooms; dz <= this.maxRooms; dz++) {
				final int chunkX = dx * 5;
				final int chunkZ = dz * 5;

				if ((dx == 0 && dz == 0)) {
					// Skip the entrance location
					continue;
				}

				// Place a room
				this.placeRoom(world, chunkX, chunkZ, random);

				// Skip the horizontal corridor to the left of the entrance
				if (dx < this.maxRooms && !(dx == -1 && dz == 0))
					// Place horizontal corridor to the right of the room (90 degrees rotation)
					this.placeCorridor(world, (chunkX << 4) + 32, (chunkZ << 4) + 15, random, Rotation.CLOCKWISE);

				if (dz < this.maxRooms)
					// Place vertical corridor to the right of the room (no rotation)
					this.placeCorridor(world, (chunkX << 4), (chunkZ << 4) + 32, random, Rotation.NONE);
			}
		}
	}

	private void placeEntrance(final World world, final Random random) {
		final File folder = this.theme.getFolder(DungeonRegionType.ENTRANCE);
		final File entrance = this.getRandomFile(folder, random);

		this.place(new Location(world, -16, STRUCTURE_PLACEMENT_Y, -16), entrance, Rotation.NONE, world);
	}

	private void placeRoom(final World world, final int chunkX, final int chunkZ, final Random random) {
		final boolean isBossRoom = (this.objective instanceof KillMonstersObjective) && random.nextInt(20) == 0;

		final File folder;
		final File room;
		if (isBossRoom) {
			folder = this.theme.getFolder(DungeonRegionType.BOSS_ROOM);
			room = this.getRandomFile(folder, random);
			((KillMonstersObjective) this.objective).setBossRoomPlaced(true);
		} else {
			folder = this.theme.getFolder(DungeonRegionType.ROOM);
			room = this.getRandomFile(folder, random);
		}

		this.place(new Location(world, (chunkX << 4) - 16, STRUCTURE_PLACEMENT_Y, (chunkZ << 4) - 16), room, Rotation.NONE, world);
	}

	private void placeCorridor(final World world, final int x, final int z, final Random random, final Rotation rotation) {
		final File folder = this.theme.getFolder(DungeonRegionType.CORRIDOR);
		final File corridor = this.getRandomFile(folder, random);

		this.place(new Location(world, x, STRUCTURE_PLACEMENT_Y, z), corridor, rotation, world);
	}

	private void place(final Location location, final File file, final Rotation rotation, final World world) {
		if (!file.exists())
			return;

		final Clipboard clipboard;
		try (final ClipboardReader reader = BuiltInClipboardFormat.FAST.getReader(new FileInputStream(file))) {
			clipboard = reader.read();
		} catch (final IOException e) {
			Common.error(e);
			return;
		}

		try (final EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))) {
			final ClipboardHolder holder = new ClipboardHolder(clipboard);
			holder.setTransform(new AffineTransform().rotateY(rotation == Rotation.CLOCKWISE ? 90 : 0));

			final Operation operation = holder
					.createPaste(editSession)
					.to(BukkitAdapter.asBlockVector(location))
					.build();

			Operations.complete(operation);
		}
	}

	private File getRandomFile(final File folder, final Random random) {
		final File[] files = folder.listFiles();
		if (files == null || files.length == 0)
			throw new IllegalArgumentException("Folder is empty");

		return files[random.nextInt(files.length)];
	}
}
