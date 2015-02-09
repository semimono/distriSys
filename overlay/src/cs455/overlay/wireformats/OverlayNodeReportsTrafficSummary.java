package cs455.overlay.wireformats;

import cs455.overlay.transport.TCPConnection;

import java.io.*;

/**
 * Created by Cullen on 1/25/2015.
 */
public class OverlayNodeReportsTrafficSummary implements Event {


	public int totalPacketsSent;
	public int totalPacketsRelayed;
	public int totalPacketsReceived;
	public long dataSent;
	public long dataReceived;

	public OverlayNodeReportsTrafficSummary(int totalPacketsSent, int totalPacketsRelayed, int totalPacketsReceived, long dataSent, long dataReceived) {
		this.totalPacketsSent = totalPacketsSent;
		this.totalPacketsRelayed = totalPacketsRelayed;
		this.totalPacketsReceived = totalPacketsReceived;
		this.dataSent = dataSent;
		this.dataReceived = dataReceived;
	}

	public OverlayNodeReportsTrafficSummary(DataInputStream dataIn) throws IOException {
		totalPacketsSent = dataIn.readInt();
		totalPacketsRelayed = dataIn.readInt();
		dataSent = dataIn.readLong();
		totalPacketsReceived = dataIn.readInt();
		dataReceived = dataIn.readLong();
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dataOut.writeByte(Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY);
		dataOut.writeInt(totalPacketsSent);
		dataOut.writeInt(totalPacketsRelayed);
		dataOut.writeLong(dataSent);
		dataOut.writeInt(totalPacketsReceived);
		dataOut.writeLong(dataReceived);

		dataOut.flush();
		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dataOut.close();
		return marshalledBytes;
	}

	@Override
	public void execute(TCPConnection con) {

	}
}
