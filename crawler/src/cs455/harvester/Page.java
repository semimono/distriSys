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
	private boolean broken;
	private boolean explored;
	private int depth;

	public Page(URL target, int depth) {
		this.target = target;
		this.depth = depth;
		this.broken = false;
		links = new HashSet<URL>();
		externalFrom = new HashSet<URL>();
		explored = false;
	}

	public synchronized boolean explore() {
		if (explored)
			return false;
		explored = true;
		return true;
	}

	public boolean valid() {
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

	public synchronized void resetExplored(int depth) {
		this.depth = depth;
		explored = false;
	}
	public int getDepth() {
		return depth;
	}

	public void setBroken(boolean broken) {
		this.broken = broken;
	}
	public boolean isBroken() {
		return broken;
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
