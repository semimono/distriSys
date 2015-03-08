package cs455.overlay.node;

import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPConnectionsCache;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.InteractiveCommandParser;
import cs455.overlay.util.StatisticsCollectorAndDisplay;
import cs455.overlay.wireformats.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Random;

/**
 * Created by Cullen on 1/22/2015.
 */
public class MessagingNode {

	private static MessagingNode node = null;

	private static final long MESSAGING_FINISHED_DELAY_MILLIS = 3000;
	private static final String COMMAND_PRINT_STATISTICS = "print-counters-and-diagnostics";
	private static final String COMMAND_EXIT_OVERLAY = "exit-overlay";

	private TCPServerThread server;
	private TCPConnection registryCon;
	private int id;
	private InteractiveCommandParser commandParser;
	private RoutingTable table;
	private List<Integer> nodeIds;
	private long lastReceived;

	// messaging statistics
	private int totalPacketsSent;
	private int totalPacketsRelayed;
	private int totalPacketsReceived;
	private long dataSent;
	private long dataReceived;

	private MessagingNode(String host, int port) throws UnknownHostException {
		id = -1;
		table = null;
		nodeIds = null;
		lastReceived = 0;
		resetStatistics();

		Runtime.getRuntime().addShutdownHook(new ShutdownHook());
		try {
			if (host.equalsIgnoreCase("localhost"))
				host = InetAddress.getLocalHost().getHostAddress();
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
		commandParser.addCommand(COMMAND_PRINT_STATISTICS, "ps");
		commandParser.addCommand(COMMAND_EXIT_OVERLAY, "eo");
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

	public synchronized void setRoutingTable(RoutingTable newTable) throws IOException {
		if (table != null) {
			for (RoutingEntry entry: table.entries) {
				if (entry.conn != null) {
					try {
						entry.conn.close();
					} catch (IOException e) {
						System.err.println("Failed to close old TCP connection");
					}
				}
			}
		}
		this.table = newTable;
		for(RoutingEntry entry: table.entries) {
			entry.conn = new TCPConnection(new Socket(entry.address, entry.port));
		}
	}

	public void resetStatistics() {
		totalPacketsSent = 0;
		totalPacketsRelayed = 0;
		totalPacketsReceived = 0;
		dataSent = 0;
		dataReceived = 0;
	}

	public void setNodeIdList(List<Integer> nodeIds) {
		this.nodeIds = nodeIds;
		this.nodeIds.remove((Integer)id);
	}

	public OverlayNodeReportsTrafficSummary getTrafficSummary() {
		return new OverlayNodeReportsTrafficSummary(id, totalPacketsSent, totalPacketsRelayed, totalPacketsReceived, dataSent, dataReceived);
	}

	public void receiveMessage(OverlayNodeSendsData message) {
		lastReceived = System.currentTimeMillis();
		if (message.destinationId == id) {
			synchronized(this) {
				++totalPacketsReceived;
				dataReceived += message.payload;
			}
		} else {
			message.nodeTrace.add(id);
			sendMessage(message);
			synchronized(this) {
				++totalPacketsRelayed;
			}
		}
	}

	public void sendMessage(OverlayNodeSendsData message) {
		// find node to send to
		boolean lessThan = message.destinationId < id;
		int bestOption = 0;
		for(int i=0; i<table.entries.length; ++i) {
			if (lessThan) {
				if (table.entries[i].id > id || table.entries[i].id <= message.destinationId) {
					bestOption = i;
				} else {
					break;
				}
			} else {
				if (table.entries[i].id > id && table.entries[i].id <= message.destinationId) {
					bestOption = i;
				} else {
					break;
				}
			}
		}

		// send message
		try {
			table.entries[bestOption].conn.send(message);
		} catch (IOException e) {
			System.err.println("Failed to send message to " +table.entries[bestOption].id);
		}
	}

	public void startMessaging(int messageCount) {
		Random rand = new Random();
		System.out.println("Started sending messages.");
		for(int i=0; i<messageCount; ++i) {
			int destId = nodeIds.get(rand.nextInt(nodeIds.size()));
			int payload = rand.nextInt();
			OverlayNodeSendsData message = new OverlayNodeSendsData(destId, id, payload);
			sendMessage(message);
			++totalPacketsSent;
			dataSent += payload;
		}
		lastReceived = System.currentTimeMillis();
		while(lastReceived + MESSAGING_FINISHED_DELAY_MILLIS > System.currentTimeMillis()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				break;
			}
		}
	}

	public int getPort() {
		return server.getPort();
	}

	private void run() throws IOException {
		registryCon.send(new OverlayNodeSendsRegistration(InetAddress.getLocalHost(), getPort()));
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
			printStats();
		} else if (command[0].equals(COMMAND_EXIT_OVERLAY)) {
			System.exit(1);
		}
	}

	public void printStats() {
		StatisticsCollectorAndDisplay stats = new StatisticsCollectorAndDisplay();
		stats.addStat(getTrafficSummary(), id);
		System.out.println(stats.header());
		System.out.println(stats.getNode(id));
	}

	public synchronized void close() throws IOException, InterruptedException {
		if (registryCon != null && registryCon.isAlive() && id > -1) {
			registryCon.send(new OverlayNodeSendsDeregistration(InetAddress.getLocalHost(), server.getPort(), id));
			registryCon.join(10000);
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

		// run
		node = new MessagingNode(registryAddress, port);
		node.run();
	}

	private static void showUsageAndExit() {
		System.out.println("Usage: java cs455.overlay.node.MessagingNode <registry address> <registry port>");
		System.exit(-1);
	}

	private class ShutdownHook extends Thread {

		@Override
		public void run() {
			try {
				MessagingNode.get().close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
