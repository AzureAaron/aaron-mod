package net.azureaaron.mod.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.IntFunction;

import com.google.common.base.Preconditions;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.azureaaron.mod.utils.render.Renderer;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;

/**
 * Provides task scheduling based on the client tick.
 */
public class Scheduler {
	public static final Scheduler INSTANCE = new Scheduler();
	private static final IntFunction<ArrayList<Runnable>> NEW_TASK_LIST = _int -> new ArrayList<>();
	/** tick → tasks */
	private final Int2ObjectMap<List<Runnable>> tasks = new Int2ObjectOpenHashMap<>();
	private int currentTick = 0;

	//No instances for you!
	private Scheduler() {}

	/**
	 * @see #schedule(Runnable, int, boolean)
	 */
	public void schedule(Runnable task, int delay) {
		schedule(task, delay, false);
	}

	/**
	 * Schedules a task that will be ran after a certain delay has passed.
	 * 
	 * @param task          the task to run
	 * @param delay         the delay (in ticks) after which the task will be executed
	 * @param multithreaded whether the task should be executed on a separate thread
	 */
	public void schedule(Runnable task, int delay, boolean multithreaded) {
		Objects.requireNonNull(task, "Cannot schedule a null task!");
		Preconditions.checkArgument(delay >= 0, "Cannot schedule a task in the past!");
		Renderer.assertOnRenderThread("Scheduler called from outside the Render Thread");

		addTask(new Task(task, delay, false, multithreaded), currentTick + delay);
	}

	/**
	 * @see #scheduleCyclic(Runnable, int, boolean)
	 */
	public void scheduleCyclic(Runnable task, int period) {
		scheduleCyclic(task, period, false);
	}

	/**
	 * Schedules a task that will be ran every {@code period} ticks.
	 * 
	 * @param task          the task to run
	 * @param period        the interval between executions (in ticks)
	 * @param multithreaded whether the task should be executed on a separate thread
	 */
	public void scheduleCyclic(Runnable task, int period, boolean multithreaded) {
		Objects.requireNonNull(task, "Cannot schedule a null task!");
		Preconditions.checkArgument(period >= 1, "Cannot schedule a cyclic task with a period shorter than 1!"); //Due to re-scheduling
		Renderer.assertOnRenderThread("Scheduler called from outside the Render Thread");

		addTask(new Task(task, period, true, multithreaded), currentTick);
	}

	/**
	 * Ticks the scheduler at the end of each client tick.
	 * 
	 * Should only ever be called from the main class!
	 */
	public void tick() {
		//Profile the scheduler tick for debugging
		Profiler profiler = Profilers.get();
		profiler.push("aaronModSchedulerTick");

		//Check if there are tasks scheduled for the current tick, if there are any then execute them.
		if (tasks.containsKey(currentTick)) {
			List<Runnable> tasks4Tick = tasks.get(currentTick);

			//Run each task
			for (Runnable task : tasks4Tick) {
				task.run();
			}

			//Remove the entry for the tasks for this tick from the map
			tasks.remove(currentTick);
		}

		//Increment current tick
		currentTick++;
		profiler.pop();
	}

	public int getCurrentTick() {
		return currentTick;
	}

	/**
	 * Schedules a task to be run at a certain tick. Note that the caller must account for the current tick themselves.
	 */
	private void addTask(Runnable task, int ticks) {
		tasks.computeIfAbsent(ticks, NEW_TASK_LIST).add(task);
	}

	private record Task(Runnable task, int period, boolean cyclic, boolean multithreaded) implements Runnable {
		@Override
		public void run() {
			if (multithreaded) {
				CompletableFuture.runAsync(task);
			} else {
				task.run();
			}

			//If the task is cyclic then schedule it to be ran again
			if (cyclic) INSTANCE.addTask(this, INSTANCE.currentTick + period);
		}
	}
}
