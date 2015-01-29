package cs455.overlay.wireformats;

import java.io.*;

/**
 * Created by Cullen on 1/25/2015.
 */
public class OverlayNodeReportsTrafficSummary implements Event {


	public OverlayNodeReportsTrafficSummary() {
	}

	public OverlayNodeReportsTrafficSummary(DataInputStream dataIn) throws IOException {
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dataOut.writeByte(Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY);

		dataOut.flush();
		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dataOut.close();
		return marshalledBytes;
	}
}
