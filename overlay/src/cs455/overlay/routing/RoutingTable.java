package cs455.overlay.routing;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cullen on 1/25/2015.
 */
public class RoutingTable {

	public RoutingEntry[] entries;

	public RoutingTable(int size) {
		entries = new RoutingEntry[size];
	}

	public int size() {
		return entries.length;
	}
}
