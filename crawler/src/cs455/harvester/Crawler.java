package cs455.harvester;

import cs455.harvester.transport.TCPConnection;
import cs455.harvester.transport.TCPServerThread;
import cs455.harvester.util.OutputGenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * Created by Cullen on 2/21/2015.
 */
public class Crawler {

//	public static final String[] VALID_PREFIXES = new String[] {
//		"http://www.bmb.colostate.edu",///index.cfm",
//		"http://www.biology.colostate.edu",///",
//		"http://www.chm.colostate.edu",///",
//		"http://www.cs.colostate.edu",///cstop/index.html",
//		"http://www.math.colostate.edu",///",
//		"http://www.physics.colostate.edu",///",
//		"http://www.colostate.edu",///Depts/Psychology/",
//		"http://www.stat.colostate.edu"///"
//	};
	public static final String[] VALID_EXTENSIONS = new String[] {
		".html", ".htm", ".cfm", ".asp", ".php", ".jsp"
	};
	public static final int MAX_RECURSION_DEPTH = 5;
	public static final long INITIAL_WAIT_MILLIS = 1000; // TODO: increase back up to 10 seconds
	private static Crawler singleton;

	private TCPServerThread server;
	private ThreadPool pool;
	private URL root;
	private Map<URL, Page> pages;
	private List<RemoteCrawler> siblings;

	private Crawler(URL root, int threadCount, int port, String configFile) throws IOException {
//		synchronized(this) {
			this.root = root;
			pool = new ThreadPool(threadCount);
			pages = new HashMap<URL, Page>();
			siblings = new ArrayList<RemoteCrawler>();

			readConfig(configFile);
			server = new TCPServerThread(port);

			synchronized (Crawler.class) {
				if (singleton != null)
					throw new RuntimeException("Created singleton Crawler multiple times.");
				singleton = this;
			}
			server.start();
//		}
	}
	private static Crawler init(URL root, int threadCount, int port, String configFile) throws IOException {
		return new Crawler(root, threadCount, port, configFile);
	}

	private synchronized void start() throws InterruptedException {
		// begin crawling
		pool.start();
		try {
			pool.add(new Task(addPage(root, 0)));
		} catch (InterruptedException e) {
		}

		// wait for all threads to complete
		while(true) {
			Thread.sleep(2000);
			if (!pool.idle()) continue;
			boolean done = true;
			for(RemoteCrawler c: siblings) {
				if (!c.finished) {
					done = false;
					break;
				}
			}
			if (done)
				break;
		}

		// write output
		OutputGenerator out = new OutputGenerator(pages, root);
		;


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
	public synchronized List<RemoteCrawler> getSiblings() {
		return new ArrayList<RemoteCrawler>(siblings);
	}

	public synchronized Page addPage(URL url, int depth) {
		if (url.toString().endsWith("/")) try {
			url = new URL(url.toString().replaceFirst("/+$", ""));
		} catch (MalformedURLException e) {}
		Page page = pages.get(url);
		if (page == null) {
			page = new Page(url, depth);
			pages.put(url, page);
//		} else if (page.getDepth() > depth) {
//			page.resetExplored(depth);
		}
		return page;
	}
	public synchronized boolean pageFound(URL target) {
		return pages.containsKey(target);
	}

	public void addTask(Task task) throws InterruptedException {
		pool.add(task);
	}

	public synchronized void addConnection(TCPConnection con, URL root) {
		for(RemoteCrawler c: siblings) {
			if (c.getRoot().getHost().equals(root.getHost())) {
				c.setConnection(con);
			}
		}
	}



	private synchronized void readConfig(String fileName) throws FileNotFoundException {
		Scanner in = new Scanner(new File(fileName));
		try {
			while(in.hasNextLine()) {
				String line = in.nextLine();
				String host = line.replaceFirst(":.*", "");
				line = line.replace(host +":", "");
				int port = Integer.parseInt(line.replaceFirst(",.*", ""));
				String url = line.replaceFirst(".+?,", "");
				siblings.add(new RemoteCrawler(host, port, new URL(url)));
			}
		} catch (NumberFormatException e) {
			System.err.println("Improperly formatted configuration file: " +fileName);
			System.exit(-1);
		} catch (MalformedURLException e) {
			System.err.println("Improperly formatted configuration file: " +fileName);
			System.exit(-1);
		}
	}


	//////////////////////
	// Program Entrance //
	//////////////////////
	public static void main(String[] args) throws InterruptedException, IOException {
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
		init(rootUrl, threadCount, port, configFile);

		Thread.sleep(INITIAL_WAIT_MILLIS);
		get().start();

	}

	private static void showUsageAndExit() {
		System.out.println("Usage: java cs455.harvester.Crawler <port> <thread count> <root url> <config file>");
		System.exit(-1);
	}
}
