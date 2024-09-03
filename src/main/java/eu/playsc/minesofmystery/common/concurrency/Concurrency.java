package eu.playsc.minesofmystery.common.concurrency;

import eu.playsc.minesofmystery.MinesOfMystery;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Concurrency {
	public static void cancelTasks() {
		Bukkit.getScheduler().cancelTasks(MinesOfMystery.getInstance());
	}

	public static SimpleTask runLater(final Runnable task) {
		return runLater(1, task);
	}

	public static SimpleTask runLater(final int delayTicks, final Runnable runnable) {
		if (runIfDisabled(runnable))
			return null;

		try {
			final BukkitTask task;

			if (runnable instanceof BukkitRunnable)
				task = ((BukkitRunnable) runnable).runTaskLater(MinesOfMystery.getInstance(), delayTicks);
			else
				task = Bukkit.getScheduler().runTaskLater(MinesOfMystery.getInstance(), runnable, delayTicks);

			final SimpleTask simpleTask = SimpleTask.fromBukkit(task);

			if (runnable instanceof SimpleRunnable)
				((SimpleRunnable) runnable).setupTask(simpleTask);

			return simpleTask;
		} catch (final NoSuchMethodError err) {
			return SimpleTask.fromBukkit(Bukkit.getScheduler().scheduleSyncDelayedTask(MinesOfMystery.getInstance(), runnable, delayTicks), false);
		}
	}

	public static SimpleTask runAsync(final Runnable task) {
		return runLaterAsync(0, task);
	}

	public static SimpleTask runLaterAsync(final int delayTicks, final Runnable runnable) {
		if (runIfDisabled(runnable))
			return null;

		try {
			final BukkitTask task;

			if (runnable instanceof BukkitRunnable)
				task = ((BukkitRunnable) runnable).runTaskLaterAsynchronously(MinesOfMystery.getInstance(), delayTicks);
			else
				task = Bukkit.getScheduler().runTaskLaterAsynchronously(MinesOfMystery.getInstance(), runnable, delayTicks);

			final SimpleTask simpleTask = SimpleTask.fromBukkit(task);

			if (runnable instanceof SimpleRunnable)
				((SimpleRunnable) runnable).setupTask(simpleTask);

			return simpleTask;
		} catch (final NoSuchMethodError err) {
			return SimpleTask.fromBukkit(Bukkit.getScheduler().scheduleAsyncDelayedTask(MinesOfMystery.getInstance(), runnable, delayTicks), true);
		}
	}

	public static SimpleTask runTimer(final int repeatTicks, final Runnable task) {
		return runTimer(0, repeatTicks, task);
	}

	public static SimpleTask runTimer(final int delayTicks, final int repeatTicks, final Runnable runnable) {
		if (runIfDisabled(runnable))
			return null;

		try {
			final BukkitTask task;

			if (runnable instanceof BukkitRunnable)
				task = ((BukkitRunnable) runnable).runTaskTimer(MinesOfMystery.getInstance(), delayTicks, repeatTicks);
			else
				task = Bukkit.getScheduler().runTaskTimer(MinesOfMystery.getInstance(), runnable, delayTicks, repeatTicks);

			final SimpleTask simpleTask = SimpleTask.fromBukkit(task);

			if (runnable instanceof SimpleRunnable)
				((SimpleRunnable) runnable).setupTask(simpleTask);

			return simpleTask;

		} catch (final NoSuchMethodError err) {
			return SimpleTask.fromBukkit(Bukkit.getScheduler().scheduleSyncRepeatingTask(MinesOfMystery.getInstance(), runnable, delayTicks, repeatTicks), false);
		}
	}

	public static SimpleTask runTimerAsync(final int repeatTicks, final Runnable task) {
		return runTimerAsync(0, repeatTicks, task);
	}

	public static SimpleTask runTimerAsync(final int delayTicks, final int repeatTicks, final Runnable runnable) {
		if (runIfDisabled(runnable))
			return null;

		try {
			final BukkitTask task;

			if (runnable instanceof BukkitRunnable)
				task = ((BukkitRunnable) runnable).runTaskTimerAsynchronously(MinesOfMystery.getInstance(), delayTicks, repeatTicks);
			else
				task = Bukkit.getScheduler().runTaskTimerAsynchronously(MinesOfMystery.getInstance(), runnable, delayTicks, repeatTicks);

			final SimpleTask simplTask = SimpleTask.fromBukkit(task);

			if (runnable instanceof SimpleRunnable)
				((SimpleRunnable) runnable).setupTask(simplTask);

			return simplTask;

		} catch (final NoSuchMethodError err) {
			return SimpleTask.fromBukkit(Bukkit.getScheduler().scheduleAsyncRepeatingTask(MinesOfMystery.getInstance(), runnable, delayTicks, repeatTicks), true);
		}
	}

	private static boolean runIfDisabled(final Runnable run) {
		if (!MinesOfMystery.getInstance().isEnabled()) {
			run.run();

			return true;
		}

		return false;
	}
}
