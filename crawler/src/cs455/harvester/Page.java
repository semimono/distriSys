package cs455.harvester;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Cullen on 3/7/2015.
 */
public class Page {

	private URL target;
	private Set<Page> links;
	private Set<String> brokenLinks;
	private boolean explored;
	private int depth;

	public Page(URL target, int depth) {
		this.target = target;
		this.depth = depth;
		links = new HashSet<Page>();
		brokenLinks = new HashSet<String>();
		explored = false;
	}

	public synchronized boolean explore() {
		if (explored)
			return false;
		explored = true;
		return true;
	}

	public boolean valid() {
		System.out.println(target.toString());
		if (target.toString().matches("//.*/.*\\.\\w*$")) {
			for(String extension: Crawler.VALID_EXTENSIONS)
				if (target.toString().endsWith(extension))
					return true;
			return false;
		}
		return true;
	}

	public URL getTarget() {
		return target;
	}

	public synchronized boolean add(Page link) {
		return links.add(link);
	}

	public synchronized boolean add(String brokenLink) {
		return brokenLinks.add(brokenLink);
	}

	public synchronized boolean remove(Page link) {
		return links.remove(link);
	}

	public synchronized boolean contains(Page link) {
		return links.contains(link);
	}

	public synchronized Set<Page> getLinks() {
		return new HashSet<Page>(links);
	}

	public synchronized Set<String> getBrokenLinks() {
		return new HashSet<String>(brokenLinks);
	}

	public int getDepth() {
		return depth;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof Page && target.equals(((Page) other).target);
	}

	@Override
	public String toString() {
		return target.toString();
	}

}
