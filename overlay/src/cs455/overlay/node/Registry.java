package cs455.overlay.node;

import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.InteractiveCommandParser;
import cs455.overlay.wireformats.RegistrySendsNodeManifest;

import java.io.IOException;
import java.net.InetAddress;
import java.util.*;

/**
 * Created by Cullen on 1/22/2015.
 */
public class Registry {

	public static final int MAXIMUM_MASSAGING_NODES = 10;

	public static final String COMMAND_LIST_NODES = "list-messaging-nodes";
	public static final String COMMAND_SETUP_OVERLAY = "setup-overlay";
	public static final String COMMAND_LIST_TABLES = "list-routing-tables";
	public static final String COMMAND_START = "start";

	private static Registry reg = null;

	private SortedMap<Integer, Node> nodes;
	private List<Integer> freeIds;
	private Random rand;
	private TCPServerThread server;
	private InteractiveCommandParser commandParser;
	private boolean tablesDistributed;


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

		// setup command parser
		commandParser = new InteractiveCommandParser(
			""
		);
		commandParser.addCommand(COMMAND_LIST_NODES, new String[] {});
		commandParser.addCommand(COMMAND_LIST_TABLES, new String[] {});
		commandParser.addCommand(COMMAND_SETUP_OVERLAY, new String[] {"number-of-routing-table-entries"});
		commandParser.addCommand(COMMAND_START, new String[] {"number-of-messages"});

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

	public Node register(InetAddress address, int port, TCPConnection con) {
		Node node = new Node(address, port, con);
		if (freeIds.size() < 1)
			return node;
		for(Node entry: nodes.values()) {
			if (entry.address.equals(address) && entry.port == port)
				return node;
		}
		node.id = freeIds.get(rand.nextInt(freeIds.size()));
		freeIds.remove((Integer) node.id);
		nodes.put(node.id, node);
		tablesDistributed = false;
		return node;
	}

	public void deregister(int id) throws IOException {
		if (!nodes.containsKey(id))
			return;
		nodes.remove(id).connection.close();
		freeIds.add(id);
	}

	public void nodeSetUp(int id) {
		if (id < 0) {
			System.err.println("Overlay");
		}
		Node node = nodes.get(id);
		if (node == null)
			System.err.println("Received overlay setup response from unknown messaging node");
		else
			node.setup = true;
	}


	private void run() {
		server.start();
		System.out.println("Started listening for connections on port " +server.getPort());

		// handle console commands
		while(true) {
			String[] command = commandParser.receiveCommand();
			handleCommand(command);
		}
	}

	private void handleCommand(String[] command) {
		if (command == null || command.length < 1)
			return;
		if (command[0].equals(COMMAND_LIST_NODES)) {
			listNodes();
		} else if (command[0].equals(COMMAND_LIST_TABLES)) {
			listTables();
		} else if (command[0].equals(COMMAND_SETUP_OVERLAY)) {
			setupOverlay(Integer.parseInt(command[1]));
		} else if (command[0].equals(COMMAND_START)) {
			startMessaging(Integer.parseInt(command[1]));
		}
	}

	private void listNodes() {
		for(Node node: nodes.values()) {
			System.out.println(node.address +":" +node.port +" id: " +node.id);
		}
	}

	private void listTables() {
		for(Node node: nodes.values()) {
			
		}
	}

	private synchronized void setupOverlay(int tableSize) {
		// error checking
		if (tableSize < 1) {
			System.err.println("number-of-routing-table-entries parameter must be larger than 0");
			return;
		}
		if (nodes.size() < 2) {
			System.err.println("Must have at least 2 nodes connected before setting up the overlay");
			return;
		}
		if (Math.pow(2, tableSize -1) >= nodes.size()) {
			System.err.println("number-of-routing-table-entries parameter too large for the number of nodes");
			return;
		}

		tablesDistributed = true;
		Integer[] list = (Integer[]) nodes.keySet().toArray();
		Arrays.sort(list);
		for (int i=0; i<list.length; ++i) {
			RoutingTable table = new RoutingTable(tableSize);
			int offset = 1;
			for(int j=0; j<tableSize; ++j) {
				Node targetNode = nodes.get(list[(i +offset) %list.length]);
				table.entries[j] = new RoutingEntry(targetNode.id, targetNode.address, targetNode.port);
				offset *= 2;
			}

			RegistrySendsNodeManifest message = new RegistrySendsNodeManifest(table, list);
			Node node = nodes.get(list[i]);
			node.table = table;
			try {
				node.connection.send(message);
			} catch (IOException e) {
				System.err.println("Failed to send routing table to node " +node.id +" at " +node.address +":" +node.port);
				tablesDistributed = false;
			}
		}
	}

	private void startMessaging(int messageCount) {

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
