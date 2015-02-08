package cs455.overlay.wireformats;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.transport.TCPConnection;

import java.io.*;

/**
 * Created by Cullen on 1/25/2015.
 */
public class RegistryRequestsTaskInitiate implements Event {

	public int packetCount;

	public RegistryRequestsTaskInitiate(int packetCount) {
		this.packetCount = packetCount;
	}

	public RegistryRequestsTaskInitiate(DataInputStream dataIn) throws IOException {
		packetCount = dataIn.readInt();
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dataOut.writeByte(Protocol.REGISTRY_REQUESTS_TASK_INITIATE);
		dataOut.writeInt(packetCount);

		dataOut.flush();
		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dataOut.close();
		return marshalledBytes;
	}

	@Override
	public void execute(TCPConnection con) {
		MessagingNode.get();
	}
}
