package eu.playsc.minesofmystery.common.concurrency;

import org.bukkit.scheduler.BukkitTask;

public abstract class SimpleRunnable implements Runnable {

	private BukkitTask task;

	public final synchronized void cancel() throws IllegalStateException {
		this.checkScheduled();
		this.task.cancel();
	}

	public final synchronized BukkitTask runTask() throws IllegalArgumentException, IllegalStateException {
		this.checkNotYetScheduled();
		return this.setupTask(Concurrency.runLater(this));
	}

	public final synchronized BukkitTask runTaskAsync() throws IllegalArgumentException, IllegalStateException {
		this.checkNotYetScheduled();
		return this.setupTask(Concurrency.runAsync(this));
	}

	public final synchronized BukkitTask runTaskLater(final long delay) throws IllegalArgumentException, IllegalStateException {
		this.checkNotYetScheduled();
		return this.setupTask(Concurrency.runLater((int) delay, this));
	}

	public final synchronized BukkitTask runTaskLaterAsynchronously(final long delay) throws IllegalArgumentException, IllegalStateException {
		this.checkNotYetScheduled();
		return this.setupTask(Concurrency.runLaterAsync((int) delay, this));
	}

	public final synchronized BukkitTask runTaskTimer(final long delay, final long period) throws IllegalArgumentException, IllegalStateException {
		this.checkNotYetScheduled();
		return this.setupTask(Concurrency.runTimer((int) delay, (int) period, this));
	}

	public final synchronized BukkitTask runTaskTimerAsynchronously(final long delay, final long period) throws IllegalArgumentException, IllegalStateException {
		this.checkNotYetScheduled();
		return this.setupTask(Concurrency.runTimerAsync((int) delay, (int) period, this));
	}

	private void checkScheduled() {
		if (this.task == null)
			throw new IllegalStateException("Not scheduled yet");
	}

	private void checkNotYetScheduled() {
		if (this.task != null)
			throw new IllegalStateException("Already scheduled");
	}

	public BukkitTask setupTask(final BukkitTask task) {
		this.task = task;

		return task;
	}
}
