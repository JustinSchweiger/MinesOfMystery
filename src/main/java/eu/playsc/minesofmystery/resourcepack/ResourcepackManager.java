package eu.playsc.minesofmystery.resourcepack;

import eu.playsc.minesofmystery.MinesOfMystery;
import eu.playsc.minesofmystery.common.Common;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ResourcepackManager {
	@Getter
	private static final File RESOURCEPACK = new File(MinesOfMystery.getInstance().getDataFolder(), "output/generated.zip");
	private final ResourcepackServer server;
	private static byte[] hash;


	public ResourcepackManager() {
		this.init();
		this.server = new ResourcepackServer();
	}

	private void init() {
		// Copy files from resources directory to plugin folder

		this.generateResources();
		this.zipResources();
		generateHash();
	}

	public void stopServer() {
		this.server.stop();
	}

	public void generateResources() {
		Common.log("Generating resources...");
		//TODO: Handle custom blocks/items/entities/sounds

		final File targetFolder = new File(MinesOfMystery.getInstance().getDataFolder(), "resources/assets");
		if (!targetFolder.exists())
			targetFolder.mkdirs();

		final URL assetsUrl = this.getClass().getClassLoader().getResource("assets");
		if (assetsUrl == null) {
			throw new IllegalStateException("assets folder not found in resources");
		}

		final URI assetsUri;
		try {
			assetsUri = assetsUrl.toURI();
		} catch (final URISyntaxException e) {
			throw new IllegalStateException("Could not convert assets URL to URI", e);
		}

		Path assetsPath;
		try {
			assetsPath = Paths.get(assetsUri);
		} catch (final FileSystemNotFoundException e) {
			try {
				final Map<String, String> env = new HashMap<>();
				env.put("create", "true");
				assetsPath = FileSystems.newFileSystem(assetsUri, env).getPath("/assets");
			} catch (final IOException ioException) {
				throw new IllegalStateException("Could not create file system for assets", ioException);
			}
		}

		try {
			final Path finalAssetsPath = assetsPath;
			Files.walk(assetsPath).forEach(sourcePath -> {
				try {
					final Path targetPath = Paths.get(targetFolder.getPath(), sourcePath.toString().substring(finalAssetsPath.toString().length()));
					if (Files.isDirectory(sourcePath)) {
						if (!Files.exists(targetPath)) {
							Files.createDirectories(targetPath);
						}
					} else {
						Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
					}
				} catch (final IOException e) {
					throw new UncheckedIOException("Could not copy asset file", e);
				}
			});
		} catch (final IOException e) {
			throw new IllegalStateException("Could not walk assets folder", e);
		}
	}

	public void zipResources() {
		final File targetFolder = new File(MinesOfMystery.getInstance().getDataFolder(), "resources");

		if (RESOURCEPACK.exists())
			RESOURCEPACK.delete();

		if (!(new File(MinesOfMystery.getInstance().getDataFolder(), "output")).exists())
			(new File(MinesOfMystery.getInstance().getDataFolder(), "output")).mkdirs();

		try (final ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(RESOURCEPACK.toPath()))) {
			Files.walk(targetFolder.toPath()).forEach(path -> {
				if (!Files.isDirectory(path)) { // Only process files, not directories
					final String relativePath = targetFolder.toPath().relativize(path).toString().replace("\\", "/");
					final ZipEntry zipEntry = new ZipEntry(relativePath);

					try {
						zipOutputStream.putNextEntry(zipEntry);
						Files.copy(path, zipOutputStream);
						zipOutputStream.closeEntry();
					} catch (final IOException e) {
						Common.throwError("Could not add file to zip: " + path, e);
					}
				}
			});

			// Add a pack.mcmeta file with the pack format
			final ZipEntry zipEntry = new ZipEntry("pack.mcmeta");
			zipOutputStream.putNextEntry(zipEntry);
			zipOutputStream.write("{\"pack\":{\"pack_format\":23,\"description\":\"Mines of Mystery Resources\"}}".getBytes());
			zipOutputStream.closeEntry();

			// Add the logo
			final ZipEntry logoEntry = new ZipEntry("pack.png");
			zipOutputStream.putNextEntry(logoEntry);
			try (final InputStream fis = MinesOfMystery.getInstance().getResource("pack.jpg")) {
				final byte[] data = new byte[1024];
				int read;
				while ((read = fis.read(data)) != -1) {
					zipOutputStream.write(data, 0, read);
				}
			}
			zipOutputStream.closeEntry();

		} catch (final IOException e) {
			Common.throwError("Could not create zip file", e);
		}
	}

	public static byte[] getHash() {
		if (hash == null) {
			generateHash();
		}

		return hash;
	}

	private static void generateHash() {
		try (final FileInputStream fis = new FileInputStream(RESOURCEPACK)) {
			final MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
			final byte[] data = new byte[1024];
			int read;
			while ((read = fis.read(data)) != -1) {
				sha1.update(data, 0, read);
			}
			hash = sha1.digest();
		} catch (final Exception e) {
			Common.throwError(e);
		}
	}

	public void reload() {
		this.zipResources();
		generateHash();

		if (Bukkit.getServer().getOnlinePlayers().isEmpty())
			return;

		Common.log("Sending resourcepack to [" + Bukkit.getServer().getOnlinePlayers().size() + "] online players...");
		serve(Bukkit.getServer().getOnlinePlayers());
	}

	public static void serve(final Collection<? extends Player> players) {
		serve(players.toArray(new Player[0]));
	}

	public static void serve(final Player... players) {
		for (final Player player : players) {
			player.setResourcePack(
					"http://localhost:" + ResourcepackServer.getPort() + "/resourcepack.zip",
					ResourcepackManager.getHash(),
					Component.text("Mines of Mystery Resourcepack").color(TextColor.color(0xFF8839)),
					true
			);
		}
	}
}
