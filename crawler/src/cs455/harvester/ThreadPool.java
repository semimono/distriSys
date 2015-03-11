package cs455.harvester;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Cullen on 3/3/2015.
 */
public class ThreadPool extends Thread {

	public static final long THREAD_WAIT_MILLIS = 1000;
//	LinkedBlockingQueue<Runnable> tasks;
	private Queue<Runnable> tasks;
	private Queue<WorkerThread> available;
	private WorkerThread[] workers;

	public ThreadPool(int threadCount) {
		tasks = new LinkedList<Runnable>();
		available = new LinkedList<WorkerThread>();
		workers = new WorkerThread[threadCount];
	}

	public synchronized void add(Runnable task) throws InterruptedException {
		tasks.add(task);
		notify();
	}

	public void close() {
		interrupt();
		for(WorkerThread worker: workers)
			worker.interrupt();
	}

	public synchronized boolean idle() {
		return tasks.size() < 1 && available.size() == workers.length;
	}

	@Override
	public synchronized void run() {
		for(int i=0; i<workers.length; ++i) {
			workers[i] = new WorkerThread(this);
			workers[i].start();
		}
		try {
			while (!isInterrupted()) {
				while (tasks.size() < 1 || available.size() < 1) {
					if (interrupted()) return;
					wait();
				}
				Runnable task = tasks.remove();
				available.remove().giveTask(task);
			}
		} catch (InterruptedException e) {}
	}

	private class WorkerThread extends Thread {

		ThreadPool pool;
		Runnable task;
		boolean interrupted = false;

		public WorkerThread(ThreadPool pool) {
			this.pool = pool;
		}

		public synchronized void giveTask(Runnable task) {
			this.task = task;
			notify();
		}

		@Override
		public void interrupt() {
			interrupted = true;
			super.interrupt();
		}

		@Override
		public void run() {
			while(!interrupted) {
				try {
					Thread.sleep(THREAD_WAIT_MILLIS);
					synchronized(this) {
						synchronized(pool) {
							pool.available.add(this);
							pool.notify();
						}
						wait();
					}
					System.out.println("Running task.");
					task.run();
				} catch (InterruptedException e) {}
			}
		}
	}
}
