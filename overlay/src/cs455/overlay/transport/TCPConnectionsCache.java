package cs455.overlay.transport;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by Cullen on 1/25/2015.
 */
public class TCPConnectionsCache {

	private static TCPConnectionsCache cache = null;

	public Set<TCPConnection> connections;

	private TCPConnectionsCache() {
		connections = new HashSet<TCPConnection>();
	}

	public synchronized static TCPConnectionsCache get() {
		if (cache == null)
			cache = new TCPConnectionsCache();
		return cache;
	}

	public synchronized void add(TCPConnection con) {
		connections.add(con);
	}
	public synchronized void remove(TCPConnection con) {
		connections.remove(con);
	}

	public synchronized void close() throws IOException {
		Set<TCPConnection> cons = new HashSet<TCPConnection>(connections);
		for(TCPConnection con: cons)
			con.close();
	}

}
