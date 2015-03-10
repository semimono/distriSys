package cs455.harvester;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Cullen on 2/21/2015.
 */
public class Crawler {

	public static final String[] VALID_PREFIXES = new String[] {
		"http://www.bmb.colostate.edu",///index.cfm",
		"http://www.biology.colostate.edu",///",
		"http://www.chm.colostate.edu",///",
		"http://www.cs.colostate.edu",///cstop/index.html",
		"http://www.math.colostate.edu",///",
		"http://www.physics.colostate.edu",///",
		"http://www.colostate.edu",///Depts/Psychology/",
		"http://www.stat.colostate.edu"///"
	};
	public static final String[] VALID_EXTENSIONS = new String[] {
		".html", ".htm", ".cfm", ".asp", ".php", ".jsp"
	};
	public static final int MAX_RECURSION_DEPTH = 5;
	public static final long INITIAL_WAIT_MILLIS = 10000;
	private static Crawler singleton;

	private ThreadPool pool;
	private URL root;
	private String rootPrefix;
	private Map<URL, Page> pages;

	private Crawler(URL root, int threadCount) {
		synchronized(this) {
			this.root = root;
			pool = new ThreadPool(threadCount);
			pages = new HashMap<URL, Page>();
			rootPrefix = null;
			for(String prefix: Crawler.VALID_PREFIXES)
				if (root.toString().startsWith(prefix)) {
					rootPrefix = prefix;
					break;
				}
			if (rootPrefix == null)
				throw new RuntimeException("Invalid URL passed as root URL.");

			synchronized (Crawler.class) {
				if (singleton != null)
					throw new RuntimeException("Created singleton Crawler multiple times.");
				singleton = this;
			}
		}
	}
	private static Crawler init(URL root, int threadCount) {
		return new Crawler(root, threadCount);
	}

	private synchronized void start() {
		pool.start();
		try {
			pool.add(new Task(addPage(root, 0)));
		} catch (InterruptedException e) {
		}

		// wait for all threads to complete
		// write files

	}


	public static Crawler get() {
		return singleton;
	}
	public ThreadPool getPool() {
		return pool;
	}
	public URL getRoot() {
		return root;
	}
	public String getRootPrefix() {
		return rootPrefix;
	}

	public synchronized Page addPage(URL url, int depth) {
		Page page = pages.get(url);
		if (page == null) {
			page = new Page(url, depth);
			pages.put(url, page);
		}
		return page;
	}
	public synchronized boolean pageFound(URL target) {
		return pages.containsKey(target);
	}

	public void addTask(Task task) throws InterruptedException {
		pool.add(task);
	}







	//////////////////////
	// Program Entrance //
	//////////////////////
	public static void main(String[] args) throws InterruptedException, MalformedURLException {
		// parse args
		if (args.length != 4) {
			showUsageAndExit();
		}

		// parse port
		int port = 0;
		try {
			port = Integer.parseInt(args[0]);
		} catch(NumberFormatException e) {
			showUsageAndExit();
		}
		if (port <= 1024 || port >= 65536) {
			showUsageAndExit();
		}

		// parse pool size
		int threadCount = 0;
		try {
			threadCount = Integer.parseInt(args[1]);
		} catch(NumberFormatException e) {
			showUsageAndExit();
		}

		// parse root URL
		URL rootUrl = new URL(args[2]);

		// parse config file address
		String configFile = args[3];


		// start crawling
		init(rootUrl, threadCount);

		Thread.sleep(INITIAL_WAIT_MILLIS);
		get().start();

	}

	private static void showUsageAndExit() {
		System.out.println("Usage: java cs455.harvester.Crawler <port> <thread count> <root url> <config file>");
		System.exit(-1);
	}
}
