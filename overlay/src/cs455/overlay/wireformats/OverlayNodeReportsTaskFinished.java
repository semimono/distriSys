package cs455.overlay.wireformats;

import cs455.overlay.node.Registry;
import cs455.overlay.transport.TCPConnection;

import java.io.*;

/**
 * Created by Cullen on 1/25/2015.
 */
public class OverlayNodeReportsTaskFinished implements Event {

	public int nodeId;

	public OverlayNodeReportsTaskFinished(int nodeId) {
		this.nodeId = nodeId;
	}

	public OverlayNodeReportsTaskFinished(DataInputStream dataIn) throws IOException {
		nodeId = dataIn.readInt();
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dataOut.writeByte(Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED);
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
