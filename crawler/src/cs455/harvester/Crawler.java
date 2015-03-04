package cs455.harvester;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Cullen on 2/21/2015.
 */
public class Crawler implements Runnable {

	public static final String[] VALID_URLS = new String[] {
		"http://www.bmb.colostate.edu/index.cfm",
		"http://www.biology.colostate.edu/",
		"http://www.chm.colostate.edu/",
		"http://www.cs.colostate.edu/cstop/index.html",
		"http://www.math.colostate.edu/",
		"http://www.physics.colostate.edu/",
		"http://www.colostate.edu/Depts/Psychology/",
		"http://www.stat.colostate.edu/"
	};

	private URL target;

	public Crawler(URL target) {
		this.target = target;
	}


	@Override
	public void run() {
		Source page;
		try {
			page = new Source(target);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		for(Element e: page.getAllElements(HTMLElementName.A)) {
			e.getAttributeValue("href");
		}
	}


	//////////////////////
	// Program Entrance //
	//////////////////////
	public static void main(String[] args) {
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

		// parse registry address
		String registryAddress = args[2];

		// parse config file address
		String configFile = args[3];


		// start crawling

	}

	private static void showUsageAndExit() {
		System.out.println("Usage: java cs455.harvester.Crawler <port> <thread count> <root url> <config file>");
		System.exit(-1);
	}
}
