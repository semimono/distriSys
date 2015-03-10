package cs455.harvester;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Cullen on 3/7/2015.
 */
public class Page {

	private final URL target;
	private Set<URL> links;
	private Set<URL> externalFrom;
	private boolean explored;

	public Page(URL target) {
		this.target = target;
		links = new HashSet<URL>();
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

	public synchronized boolean add(URL link) {
		return links.add(link);
	}

	public synchronized boolean addExternalFrom(URL link) {
		return externalFrom.add(link);
	}

	public synchronized boolean remove(URL link) {
		return links.remove(link);
	}

	public synchronized boolean contains(URL link) {
		return links.contains(link);
	}

	public synchronized Set<URL> getLinks() {
		return new HashSet<URL>(links);
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof Page && target.equals(((Page) other).target);
	}

}
