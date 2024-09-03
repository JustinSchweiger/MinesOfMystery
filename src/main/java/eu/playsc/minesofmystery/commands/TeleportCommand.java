package eu.playsc.minesofmystery.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.wrappers.Rotation;
import eu.playsc.minesofmystery.common.commands.SimpleCommand;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.Collection;
import java.util.List;

public class TeleportCommand extends SimpleCommand {
	@Override
	@SuppressWarnings("unchecked")
	public CommandAPICommand getCommand() {
		return new CommandAPICommand("tp")
				.withAliases("teleport")
				.withArguments(new EntitySelectorArgument.ManyEntities("entities"))
				.withArguments(new LocationArgument("location", LocationType.BLOCK_POSITION, true))
				.withOptionalArguments(new RotationArgument("rotation"))
				.withOptionalArguments(new WorldArgument("world"))
				.executes((sender, args) -> {
					final Collection<Entity> entities = (Collection<Entity>) args.get("entities");
					final Location location = (Location) args.get("location");
					if (entities == null || entities.isEmpty() || location == null)
						return;

					if (args.get("rotation") != null) {
						location.setYaw(((Rotation) args.get("rotation")).getYaw());
						location.setPitch(((Rotation) args.get("rotation")).getPitch());
					}

					if (args.get("world") != null)
						location.setWorld((World) args.get("world"));

					for (final Entity entity : entities) {
						entity.teleport(location);
					}
				});
	}

	@Override
	public List<VanillaUnregister> unregisterVanillaCommands() {
		return List.of(new VanillaUnregister("tp"));
	}
}
