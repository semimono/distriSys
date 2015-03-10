package cs455.harvester.wireformats;

import cs455.overlay.node.Registry;
import cs455.harvester.transport.TCPConnection;

import java.io.*;
import java.net.InetAddress;

/**
 * Created by Cullen on 1/25/2015.
 */
public class OverlayNodeReportsTaskFinished implements Event {

	public int nodeId;
	public InetAddress address;
	public int port;

	public OverlayNodeReportsTaskFinished(int nodeId, InetAddress address, int port) {
		this.nodeId = nodeId;
		this.address = address;
		this.port = port;
	}

	public OverlayNodeReportsTaskFinished(DataInputStream dataIn) throws IOException {
		address = Protocol.readAddress(dataIn);
		port = dataIn.readInt();
		nodeId = dataIn.readInt();
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dataOut.writeByte(Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED);
		dataOut.writeByte(address.getAddress().length);
		dataOut.write(address.getAddress());
		dataOut.writeInt(port);
		dataOut.writeInt(nodeId);

		dataOut.flush();
		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dataOut.close();
		return marshalledBytes;
	}

	@Override
	public void execute(TCPConnection con) {
		synchronized (Registry.get()) {
			Registry.get().get(nodeId).completed = true;
		}
	}
}
