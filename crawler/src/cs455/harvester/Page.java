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
	private Set<String> brokenLinks;
	private boolean explored;
	private int depth;

	public Page(URL target, int depth) {
		this.target = target;
		this.depth = depth;
		links = new HashSet<URL>();
		brokenLinks = new HashSet<String>();
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

	public synchronized boolean add(URL link) {
		return links.add(link);
	}

	public synchronized boolean addBroken(String brokenLink) {
		return brokenLinks.add(brokenLink);
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
