package cs455.overlay.routing;

import cs455.harvester.transport.TCPConnection;

import java.net.InetAddress;

/**
 * Created by Cullen on 1/25/2015.
 */
public class RoutingEntry {

	public int id;
	public InetAddress address;
	public int port;
	public TCPConnection conn;

	public RoutingEntry(int id, InetAddress address, int port) {
		this.id = id;
		this.address = address;
		this.port = port;
		this.conn = null;
	}

	public String toString() {
		return id +"\t" +address +":" +port;
	}

}
