package cs455.overlay.node;

import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPConnection;

import java.net.InetAddress;

/**
 * Created by Cullen on 1/25/2015.
 */
public class Node {

	public InetAddress address;
	public int port;
	public int id;
	public TCPConnection connection;
	public RoutingTable table;
	public boolean setup;

	public Node(InetAddress address, int port, TCPConnection con) {
		this(address, port, con, -1);
	}

	public Node(InetAddress address, int port, TCPConnection con, int id) {
		this.address = address;
		this.port = port;
		this.id = id;
		connection = con;
		setup = false;
		table = null;
	}

	public String toString() {
		return id +"\t" +address +":" +port;
	}

}
