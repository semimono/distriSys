package cs455.harvester;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Cullen on 3/3/2015.
 */
public class ThreadPool {

	public static final long THREAD_WAIT_MILLIS = 1000;
	LinkedBlockingQueue<Runnable> tasks;
	WorkerThread[] workers;

	public ThreadPool(int threadCount) {
		tasks = new LinkedBlockingQueue<Runnable>();
		workers = new WorkerThread[threadCount];
		for(int i=0; i<threadCount; ++i) {
			workers[i] = new WorkerThread(this);
			workers[i].start();
		}
	}

	public void add(Runnable task) throws InterruptedException {
		tasks.put(task);
	}

	public void interrupt() {
		for(WorkerThread worker: workers)
			worker.interrupt();
	}

	private class WorkerThread extends Thread {

		ThreadPool pool;

		public WorkerThread(ThreadPool pool) {
			this.pool = pool;
		}

		@Override
		public void run() {
			while(!interrupted()) {
				try {
					Thread.sleep(THREAD_WAIT_MILLIS);
					System.out.println("Running task.");
					pool.tasks.take().run();
				} catch (InterruptedException e) {}
			}
		}
	}
}
