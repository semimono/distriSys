package cs455.overlay.transport;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Cullen on 1/25/2015.
 */
public class TCPConnectionsCache {

	private static TCPConnectionsCache cache = null;

	public List<TCPConnection> connections;

	private TCPConnectionsCache() {
		connections = new LinkedList<TCPConnection>();
	}

	public synchronized static TCPConnectionsCache get() {
		if (cache == null)
			cache = new TCPConnectionsCache();
		return cache;
	}

	public synchronized void add(TCPConnection con) {
		connections.add(con);
	}

	public synchronized void close() throws IOException {
		for(TCPConnection con: connections)
			con.close();
	}

}
