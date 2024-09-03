package eu.playsc.minesofmystery.dungeon.handlers;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import eu.playsc.minesofmystery.common.Common;
import eu.playsc.minesofmystery.common.concurrency.Concurrency;
import eu.playsc.minesofmystery.dungeon.DungeonRegionType;
import eu.playsc.minesofmystery.dungeon.DungeonTheme;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DungeonRegionHandler {
	private static DungeonRegionHandler instance;

	public static DungeonRegionHandler get() {
		if (instance == null) {
			instance = new DungeonRegionHandler();
		}

		return instance;
	}

	public boolean canSaveRegion(final Player player) {
		try {
			WorldEdit.getInstance().getSessionManager().getIfPresent(BukkitAdapter.adapt(player)).getSelection();
		} catch (final IncompleteRegionException e) {
			return false;
		}

		return true;
	}

	@SneakyThrows
	public void saveRegion(final String name, final DungeonRegionType type, final DungeonTheme theme, final Region region, final Player player) {
		final File file = new File(theme.getFolder(type), name + ".schem");
		if (!file.exists())
			file.createNewFile();
		else
			file.delete();

		Concurrency.runAsync(() -> {
			try (
					final BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
					final EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(player));
					final ClipboardWriter writer = BuiltInClipboardFormat.FAST.getWriter(new FileOutputStream(file))
			) {
				final ForwardExtentCopy copy = new ForwardExtentCopy(editSession, region, clipboard, region.getMinimumPoint());
				copy.setCopyingEntities(true);
				Operations.complete(copy);

				writer.write(clipboard);
			} catch (final IOException e) {
				Common.error(e);
			}
		});
	}

	public void loadRegion(final String name, final DungeonRegionType type, final DungeonTheme theme, final Location location) {
		final File file = new File(theme.getFolder(type), name + ".schem");
		if (!file.exists())
			return;

		Concurrency.runAsync(() -> {
			final Clipboard clipboard;
			try (final ClipboardReader reader = BuiltInClipboardFormat.FAST.getReader(new FileInputStream(file))) {
				clipboard = reader.read();
			} catch (final IOException e) {
				Common.error(e);
				return;
			}

			try (final EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(location.getWorld()))) {
				final Operation operation = new ClipboardHolder(clipboard)
						.createPaste(editSession)
						.to(BukkitAdapter.asBlockVector(location))
						.build();

				Operations.complete(operation);
			}
		});
	}

	public String[] getLoadableRegions(final DungeonRegionType type, final DungeonTheme theme) {
		if (type == null || theme == null)
			return new String[0];

		final File folder = theme.getFolder(type);
		if (!folder.exists())
			return new String[0];

		return Arrays.stream(folder.list()).toList()
				.stream()
				.filter(name -> name.endsWith(".schem"))
				.map(name -> name.replace(".schem", ""))
				.toArray(String[]::new);
	}
}
