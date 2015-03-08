package cs455.overlay.wireformats;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.transport.TCPConnection;

import java.io.*;
import java.net.InetAddress;

/**
 * Created by Cullen on 1/25/2015.
 */
public class RegistryRequestsTaskInitiate implements Event {

	public int messageCount;

	public RegistryRequestsTaskInitiate(int packetCount) {
		this.messageCount = packetCount;
	}

	public RegistryRequestsTaskInitiate(DataInputStream dataIn) throws IOException {
		messageCount = dataIn.readInt();
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dataOut.writeByte(Protocol.REGISTRY_REQUESTS_TASK_INITIATE);
		dataOut.writeInt(messageCount);

		dataOut.flush();
		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dataOut.close();
		return marshalledBytes;
	}

	@Override
	public void execute(TCPConnection con) {
		MessagingNode node = MessagingNode.get();
		node.startMessaging(messageCount);
		try {
			con.send(new OverlayNodeReportsTaskFinished(node.getId(), InetAddress.getLocalHost(), node.getPort()));
		} catch (IOException e) {
			System.err.println("Failed to report task finished to registry.");
		}
	}
}
