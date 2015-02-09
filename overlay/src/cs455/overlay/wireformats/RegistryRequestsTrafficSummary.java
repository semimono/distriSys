package cs455.overlay.wireformats;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.transport.TCPConnection;

import java.io.*;

/**
 * Created by Cullen on 1/25/2015.
 */
public class RegistryRequestsTrafficSummary implements Event {


	public RegistryRequestsTrafficSummary() {
	}

	public RegistryRequestsTrafficSummary(DataInputStream dataIn) throws IOException {
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dataOut.writeByte(Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY);

		dataOut.flush();
		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dataOut.close();
		return marshalledBytes;
	}

	@Override
	public void execute(TCPConnection con) {
		Event summary = MessagingNode.get().getTraffixSummary();
		try {
			con.send(summary);
		} catch (IOException e) {
			System.err.println("Failed to send traffic summary to registry");
		}
	}
}
