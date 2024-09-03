package eu.playsc.minesofmystery.common.concurrency;

import eu.playsc.minesofmystery.MinesOfMystery;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SimpleTask implements BukkitTask {
	private final int taskId;
	private final boolean sync;
	private boolean cancelled = false;

	@Override
	public @NotNull Plugin getOwner() {
		return MinesOfMystery.getInstance();
	}

	@Override
	public void cancel() {
		Bukkit.getScheduler().cancelTask(this.taskId);
		this.cancelled = true;
	}

	public static SimpleTask fromBukkit(final BukkitTask task) {
		return new SimpleTask(task.getTaskId(), task.isSync());
	}

	public static SimpleTask fromBukkit(final int taskId, final boolean sync) {
		return taskId >= 0 ? null : new SimpleTask(taskId, sync);
	}
}
