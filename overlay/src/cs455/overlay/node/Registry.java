package cs455.overlay.node;

import cs455.overlay.transport.TCPServerThread;

import java.io.IOException;
import java.net.InetAddress;
import java.util.*;

/**
 * Created by Cullen on 1/22/2015.
 */
public class Registry {

	public static final int MAXIMUM_MASSAGING_NODES = 10;

	private static Registry reg = null;

	private SortedMap<Integer, Node> nodes;
	private List<Integer> freeIds;
	private Random rand;
	private TCPServerThread server;


	private Registry(int port) {
		try {
			server = new TCPServerThread(port);
		} catch (IOException e) {
			System.err.println("Error: port " +port +" currently unavailable");
			System.exit(2);
		}
		rand = new Random();
		nodes = new TreeMap<Integer, Node>();
		freeIds = new LinkedList<Integer>();
		for(int i=0; i<MAXIMUM_MASSAGING_NODES; ++i)
			freeIds.add(i);
	}


	public static Registry get() {
		if (reg == null)
			throw new RuntimeException("Attempt to access Registry from MessagingNode");
		return reg;
	}

	public int nodeCount() {
		return nodes.size();
	}

	public Node get(int id) {
		return nodes.get(id);
	}

	public Node register(InetAddress address, int port) {
		Node node = new Node(address, port);
		if (freeIds.size() < 1)
			return node;
		for(Node entry: nodes.values()) {
			if (entry.address.equals(address) && entry.port == port)
				return node;
		}
		node.id = freeIds.get(rand.nextInt(freeIds.size()));
		freeIds.remove((Integer) node.id);
		nodes.put(node.id, node);
		return node;
	}

	public void deregister(int id) {
		if (!nodes.containsKey(id))
			return;
		nodes.remove(id);
		freeIds.add(id);
	}


	private void run() {
		server.start();
		System.out.println("Started listening for connections on port " +server.getPort());

		// handle console commands
	}

	private void handleCommand(String command) {

	}


	///////////////////////
	// application entry //
	///////////////////////
	public static void main(String[] args) throws IOException {

		// parse port
		int port = 0;
		if (args.length != 1) {
			showUsageAndExit();
		}
		try {
			port = Integer.parseInt(args[0]);
		} catch(NumberFormatException e) {
			showUsageAndExit();
		}
		if (port <= 1024 || port >= 65536) {
			showUsageAndExit();
		}

		reg = new Registry(port);
		reg.run();

	}

	private static void showUsageAndExit() {
		System.out.println("Usage: java cs455.overlay.node.Registry <listening port>");
		System.out.println("    the port must be a number between 1024 and 65536");
		System.exit(-1);
	}
}
