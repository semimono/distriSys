package cs455.overlay.wireformats;

import cs455.overlay.node.Registry;
import cs455.overlay.transport.TCPConnection;

import java.io.*;

/**
 * Created by Cullen on 1/25/2015.
 */
public class OverlayNodeReportsTrafficSummary implements Event {


	public int nodeId;
	public int packetsSent;
	public int packetsRelayed;
	public int packetsReceived;
	public long dataSent;
	public long dataReceived;

	public OverlayNodeReportsTrafficSummary(int nodeId, int packetsSent, int packetsRelayed, int packetsReceived, long dataSent, long dataReceived) {
		this.nodeId = nodeId;
		this.packetsSent = packetsSent;
		this.packetsRelayed = packetsRelayed;
		this.packetsReceived = packetsReceived;
		this.dataSent = dataSent;
		this.dataReceived = dataReceived;
	}

	public OverlayNodeReportsTrafficSummary(DataInputStream dataIn) throws IOException {
		nodeId = dataIn.readInt();
		packetsSent = dataIn.readInt();
		packetsRelayed = dataIn.readInt();
		dataSent = dataIn.readLong();
		packetsReceived = dataIn.readInt();
		dataReceived = dataIn.readLong();
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dataOut.writeByte(Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY);
		dataOut.writeInt(nodeId);
		dataOut.writeInt(packetsSent);
		dataOut.writeInt(packetsRelayed);
		dataOut.writeLong(dataSent);
		dataOut.writeInt(packetsReceived);
		dataOut.writeLong(dataReceived);

		dataOut.flush();
		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dataOut.close();
		return marshalledBytes;
	}

	@Override
	public void execute(TCPConnection con) {
		Registry.get().get(nodeId).trafficSummary = this;
	}
}
