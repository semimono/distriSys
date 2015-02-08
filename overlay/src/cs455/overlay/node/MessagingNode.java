package cs455.overlay.node;

import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPConnectionsCache;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.InteractiveCommandParser;
import cs455.overlay.wireformats.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by Cullen on 1/22/2015.
 */
public class MessagingNode {

	private static MessagingNode node = null;

	private static final String COMMAND_PRINT_STATISTICS = "print-counters-and-diagnostics";
	private static final String COMMAND_EXIT_OVERLAY = "exit-overlay";

	private TCPServerThread server;
	private TCPConnection registryCon;
	private int id;
	private InteractiveCommandParser commandParser;
	private RoutingTable table;
	private List<Integer> nodeIds;

	private MessagingNode(String host, int port) throws UnknownHostException {
		id = -1;
		table = null;
		nodeIds = null;
		try {
			if (host.equalsIgnoreCase("localhost"))
				host = InetAddress.getLocalHost().getHostAddress();
			System.out.println(host);
			Socket socket = new Socket(host, port);
			registryCon = new TCPConnection(socket);
		} catch (IOException e) {
			System.err.println("Error: could not create connection to registry: " +host +":" +port);
			System.exit(3);
		}
		try {
			server = new TCPServerThread();
		} catch (IOException e) {
			System.err.println("Error: could not start server");
			e.printStackTrace();
			System.exit(1);
		}

		// setup command parser
		commandParser = new InteractiveCommandParser(
			""
		);
		commandParser.addCommand(COMMAND_PRINT_STATISTICS, new String[] {});
		commandParser.addCommand(COMMAND_EXIT_OVERLAY, new String[] {});
	}

	public static MessagingNode get() {
		if (node == null)
			throw new RuntimeException("Attempt to access MessagingNode from Registry");
		return node;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		if (id < 0)
			return;
		this.id = id;
	}

	public void setRoutingTable(RoutingTable newTable) {
		this.table = newTable;
	}

	public void setNodeIdList(List<Integer> nodeIds) {
		this.nodeIds = nodeIds;
		this.nodeIds.remove((Integer)id);
	}

	private void run() throws IOException {
		registryCon.send(new OverlayNodeSendsRegistration(InetAddress.getLocalHost(), server.getPort()));
		server.start();

		// handle console commands
		while(true) {
			String[] command = commandParser.receiveCommand();
			handleCommand(command);
		}
	}

	private void handleCommand(String[] command) {
		if (command == null || command.length < 1)
			return;
		if (command[0].equals(COMMAND_PRINT_STATISTICS)) {

		} else if (command[0].equals(COMMAND_EXIT_OVERLAY)) {

		}
	}

	@Override
	public void finalize() throws IOException, InterruptedException {
		if (registryCon != null && id > -1) {
			registryCon.send(new OverlayNodeSendsDeregistration(InetAddress.getLocalHost(), server.getPort(), id));
		}
		if (server != null) {
			server.close();
		}
		TCPConnectionsCache.get().close();
	}


	///////////////////////
	// application entry //
	///////////////////////
	public static void main(String[] args) throws IOException {

		// parse args
		if (args.length != 2) {
			showUsageAndExit();
		}

		// parse registry address
		String registryAddress = args[0];

		// parse port
		int port = 0;
		try {
			port = Integer.parseInt(args[1]);
		} catch(NumberFormatException e) {
			showUsageAndExit();
		}
		if (port <= 1024 || port >= 65536) {
			showUsageAndExit();
		}

		node = new MessagingNode(registryAddress, port);
		node.run();

	}

	private static void showUsageAndExit() {
		System.out.println("Usage: java cs455.overlay.node.MessagingNode <registry address> <registry port>");
		System.exit(-1);
	}
}
