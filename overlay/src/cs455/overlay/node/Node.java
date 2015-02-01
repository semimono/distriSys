package cs455.overlay.node;

import java.net.InetAddress;

/**
 * Created by Cullen on 1/25/2015.
 */
public class Node {

	public InetAddress address;
	public int port;
	public int id;

	public Node(InetAddress address, int port) {
		this(address, port, -1);
	}

	public Node(InetAddress address, int port, int id) {
		this.address = address;
		this.port = port;
		this.id = id;
	}

}
