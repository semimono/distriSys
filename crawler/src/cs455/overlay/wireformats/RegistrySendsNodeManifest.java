package cs455.overlay.wireformats;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPConnection;

import java.io.*;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Cullen on 1/25/2015.
 */
public class RegistrySendsNodeManifest implements Event {

	public RoutingTable table;
	public List<Integer> nodeIds;

	public RegistrySendsNodeManifest(RoutingTable table, List<Integer> nodeIds) {
		this.table = table;
		this.nodeIds = nodeIds;
	}

	public RegistrySendsNodeManifest(DataInputStream dataIn) throws IOException {
		int tableSize = dataIn.readByte();
		table = new RoutingTable(tableSize);

		for(int i=0; i<tableSize; ++i) {
			int nodeId = dataIn.readInt();
			InetAddress address = Protocol.readAddress(dataIn);
			int port = dataIn.readInt();
			table.entries[i] = new RoutingEntry(nodeId, address, port);
		}

		byte nodeCount = dataIn.readByte();
		nodeIds = new ArrayList<Integer>();
		for(int i=0; i<nodeCount; ++i)
			nodeIds.add(dataIn.readInt());
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dataOut.writeByte(Protocol.REGISTRY_SENDS_NODE_MANIFEST);
		dataOut.writeByte(table.size());
		for(RoutingEntry entry: table.entries) {
			dataOut.writeInt(entry.id);
			dataOut.writeByte(entry.address.getAddress().length);
			dataOut.write(entry.address.getAddress());
			dataOut.writeInt(entry.port);
		}
		dataOut.writeByte(nodeIds.size());
		for(int id: nodeIds)
			dataOut.writeInt(id);

		dataOut.flush();
		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dataOut.close();
		return marshalledBytes;
	}

	@Override
	public void execute(TCPConnection con) {
		int status = MessagingNode.get().getId();
		String message = "Successfully updated setup info";
		try {
			MessagingNode.get().setRoutingTable(table);
		} catch (IOException e) {
			status = -1;
			message = "Failed to initiate connection routing";
			System.err.println(message);
		}
		MessagingNode.get().setNodeIdList(nodeIds);

		NodeReportsOverlaySetupStatus response = new NodeReportsOverlaySetupStatus(status, message);
		try {
			con.send(response);
		} catch (IOException e) {
			System.err.println("Failed to send response to overlay setup message");
		}
	}
}
