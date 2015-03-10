package cs455.harvester;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import java.io.IOException;
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
		Source page;
		try {
			page = new Source(target.getTarget());
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		for(Element e: page.getAllElements(HTMLElementName.A)) {

			// fetch url
			URL url;
			String href = e.getAttributeValue("href");
			if (href == null || href.length() < 1)
				continue;
			try {
				if (href.startsWith("/"))
					url = new URL(target.getTarget() + href);
				else
					url = new URL(href);
			} catch (MalformedURLException e1) {
//				e1.printStackTrace();
				target.addBroken(href);
//				System.out.println("Broken Link at " +target +": " +href);
//				System.out.println(e);
				continue;
			}

//			System.out.println(e.toString() +": " +url);

			// check domain
			boolean outside = true;
			for(String prefix: Crawler.VALID_PREFIXES) {
				if (url.toString().startsWith(prefix)) {
					outside = false;
					break;
				}
			}
			if (outside) continue;

			Page newPage = Crawler.get().addPage(url, target.getDepth() +1);
			target.add(url);

			// add job
			if (newPage.explore() && newPage.valid()) {
				try {
					Crawler.get().addTask(new Task(newPage));
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
