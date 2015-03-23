package cs455.harvester.util;

import cs455.harvester.Page;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Map;
import java.util.Stack;

/**
 * Created by Cullen on 3/11/2015.
 */
public class OutputGenerator {

	private static final String eid = "semimono";

	private Map<URL, Page> pages;
	private URL root;

	public OutputGenerator(Map<URL, Page> pages, URL root) {
		this.pages = pages;
		this.root = root;
	}

	public void write() throws FileNotFoundException {
		String outputFolder;
		outputFolder = "/tmp/" +eid +"/"+root.getHost();
		new File(outputFolder).mkdirs();
		new File(outputFolder +"/disjoint-subgraphs/").mkdirs();
		PrintWriter brokenLinks = new PrintWriter(new File(outputFolder +"/broken-links.txt"));

		// write nodes
		for(URL key: pages.keySet()) {
			Page page = pages.get(key);

			// check broken
			if (page.isBroken()) {
				brokenLinks.println(page.getTarget());
				continue;
			}

			// setup files and paths
			String path = page.getTarget().getPath().replaceAll("[/#]", "-");
			if (path.length() < 1) path = page.getTarget().getHost();
			String folder = outputFolder +"/nodes/" +path;
			new File(folder).mkdirs();
			PrintWriter out = new PrintWriter(new File(folder +"/out.txt"));
			PrintWriter in = new PrintWriter(new File(folder +"/in.txt"));

			// write to files
			for(URL link: page.getLinks()) {
				out.println(link);
			}
			for(URL link: page.getFromLinks()) {
				in.println(link);
			}
			out.close();
			in.close();
		}
		brokenLinks.close();

		// write graphs
		int currentGraph = 1;
		Stack<Page> pageStack = new Stack<Page>();
		while(pages.size() > 0) {
			PrintWriter graph = new PrintWriter(new File(outputFolder +"/disjoint-subgraphs/graph" +currentGraph +".txt"));

			pageStack.push(pages.remove(pages.keySet().iterator().next()));
			while(pageStack.size() > 0) {
				Page page = pageStack.pop();
				graph.println(page);
				for(URL link: page.getLinks()) {
					Page newPage = pages.remove(link);
					if (newPage != null) pageStack.add(newPage);
				}
				for(URL link: page.getFromLinks()) {
					Page newPage = pages.remove(link);
					if (newPage != null) pageStack.add(newPage);
				}
			}

			graph.close();
			++currentGraph;
		}
		brokenLinks.close();
	}
}
