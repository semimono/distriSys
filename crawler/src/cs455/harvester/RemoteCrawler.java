package cs455.harvester;

import cs455.harvester.transport.TCPConnection;
import cs455.harvester.wireformats.CrawlerOpensConnection;
import cs455.harvester.wireformats.Event;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.*;

/**
 * Created by Cullen on 3/10/2015.
 */
public class RemoteCrawler {

	private static Starter starterSingleton;

	private String host;
	private int port;
	private URL root;
	private TCPConnection con;
	private Queue<Event> messages;

	public boolean finished;

	public RemoteCrawler(String host, int port, URL root) {
		this.host = host;
		this.port = port;
		this.root = root;
		finished = true;
		messages = new LinkedList<Event>();
	}

	public URL getRoot() {
		return root;
	}

	public synchronized void setConnection(TCPConnection con) {
		if (this.con == null)
			this.con = con;
	}

	public void send(Event message) {
		if (con == null) {
			starter().connect(this);
			synchronized(this) {
				messages.add(message);
			}
			return;
		}
		try {
			con.send(message);
		} catch (IOException e) {}
	}

	public static void close() {
		if (starter() != null)
			starter().interrupt();
	}

	private synchronized void flush() {
		while(messages.size() > 0) {
			Event message = messages.remove();
			try {
				con.send(message);
			} catch (IOException e) {}
		}
	}



	private static Starter starter() {
		synchronized(Starter.class) {
			if (starterSingleton == null) {
				starterSingleton = new Starter();
				starterSingleton.start();
			}
		}
		return starterSingleton;
	}

	private static class Starter extends Thread {
		public static final long RETRY_WAIT_MILLIS = 10000;
		private Queue<RemoteCrawler> crawlers;
		boolean interrupted = false;

		private Starter() {
			this.crawlers = new LinkedList<RemoteCrawler>();
		}

		public synchronized void connect(RemoteCrawler crawler) {
			crawlers.add(crawler);
		}

		@Override
		public void interrupt() {
			interrupted = true;
			super.interrupt();
		}

		@Override
		public void run() {
			while(!interrupted) {
				int i;
				synchronized(this) {
					i = crawlers.size();
				}
				for(; i>0; --i) {
					RemoteCrawler c;
					synchronized(this) {
						c = crawlers.remove();
					}
					synchronized (c) {
						if (c.con != null) continue;
						try {
							c.con = new TCPConnection(new Socket(c.host, c.port));
							c.send(new CrawlerOpensConnection(Crawler.get().getRoot()));
							c.flush();
							continue;
						} catch (IOException e) {}
					}
					synchronized(this) {
						crawlers.add(c);
					}
				}
				try {
					sleep(RETRY_WAIT_MILLIS);
				} catch (InterruptedException e) {}
			}
		}
	}
}
