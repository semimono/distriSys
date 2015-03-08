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
	private boolean explored;

	public Page(URL target) {
		this.target = target;
		links = new HashSet<Page>();
		explored = false;
	}

	public synchronized boolean explore() {
		if (explored)
			return false;
		explored = true;
		return true;
	}

	public URL getTarget() {
		return target;
	}

	public synchronized boolean add(Page link) {
		return links.add(link);
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

	@Override
	public boolean equals(Object other) {
		return other instanceof Page && target.equals(((Page) other).target);
	}

}
