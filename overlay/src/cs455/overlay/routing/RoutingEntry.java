package cs455.overlay.routing;

import java.net.InetAddress;

/**
 * Created by Cullen on 1/25/2015.
 */
public class RoutingEntry {

	public int id;
	public InetAddress address;
	public int port;

	public RoutingEntry(int id, InetAddress address, int port) {
		this.id = id;
		this.address = address;
		this.port = port;
	}

}
