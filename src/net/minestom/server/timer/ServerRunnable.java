package net.minestom.server.timer;

public abstract class ServerRunnable implements Runnable {
	private boolean cancel;
	private int task;

	public synchronized boolean isCancelled() {
		return cancel;
	}

	public synchronized void cancel() {
		cancel = true;
		Scheduler.cancelTask(task);
	}

	public synchronized int getId() {
		return task;
	}

	public int runTask() {
		return task = Scheduler.run(this);
	}

	public int runRepeating(long delay, long period) {
		if (task == 0)
			return task = Scheduler.repeating(delay, period, this);
		return task;
	}

	public int runTimer(long delay, long period, long times) {
		return runRepeatingTimes(delay, period, times);
	}

	public int runRepeatingTimes(long delay, long period, long times) {
		if (task == 0)
			return task = Scheduler.repeatingTimes(delay, period, times, this);
		return task;
	}

	public int runLater(long delay) {
		if (task == 0)
			return task = Scheduler.later(delay, this);
		return task;
	}
}