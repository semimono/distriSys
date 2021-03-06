package cs455.harvester;

import cs455.harvester.wireformats.CrawlerSendsPage;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Cullen on 3/7/2015.
 */
public class Task implements Runnable {

	private final Page target;

	public Task(Page target) {
		this.target = target;
	}

	@Override
	public void run() {
		if (target.getDepth() >= Crawler.MAX_RECURSION_DEPTH) return;
		Source page;
		System.out.println("Getting page: " +target.getTarget());
		try {
			HttpURLConnection con = (HttpURLConnection)(target.getTarget().openConnection());
			con.connect();
			InputStream is = con.getInputStream();
			page = new Source(is);
		} catch (IOException e) {
			target.setBroken(true);
			return;
		}

		int unexploredLinks = 0;
		for(Element e: page.getAllElements(HTMLElementName.A)) {

			// fetch url
			URL url;
			String href = e.getAttributeValue("href");
			if (href == null || href.length() < 1)
				continue;
			try {
				if (href.startsWith("/"))
					url = new URL(target.getTarget().getProtocol() +"://" +target.getTarget().getHost() + href);
				else
					url = new URL(href);
			} catch (MalformedURLException e1) {
				continue;
			}
			if (url.toString().endsWith("/")) try {
				url = new URL(url.toString().replaceFirst("/+$", ""));
			} catch (MalformedURLException exc) {}
			target.add(url);

			// add url to job queue
			if (url.getHost().equals(Crawler.get().getRoot().getHost())) {
				Page newPage = Crawler.get().addPage(url, target.getDepth() + 1);
				newPage.addFrom(target.getTarget());

				// add job
				if (newPage.explore() && newPage.getDepth() < Crawler.MAX_RECURSION_DEPTH) {
					++unexploredLinks;
					try {
						Crawler.get().addTask(new Task(newPage));
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			} else {
				// check domain
				RemoteCrawler crawler = null;
				for(RemoteCrawler c: Crawler.get().getSiblings()) {
					if (url.getHost().equals(c.getRoot().getHost())) {
						crawler = c;
						break;
					}
				}
				if (crawler == null) continue;
//				System.out.println("SENT MESSAGE!");
				crawler.send(new CrawlerSendsPage(url, target.getTarget()));
			}
		}
		System.out.println("Found " +unexploredLinks +" unexplored links at: " +target.getTarget());
	}
}
