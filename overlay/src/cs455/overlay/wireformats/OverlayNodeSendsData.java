package cs455.overlay.wireformats;

import cs455.overlay.transport.TCPConnection;

import java.io.*;

/**
 * Created by Cullen on 1/25/2015.
 */
public class OverlayNodeSendsData implements Event {

	public int destinationId;
	public int sourceId;
	public int payload;

	public int[] nodeTrace;

	public OverlayNodeSendsData(int destinationId, int sourceId, int payload, int[] nodeTrace) {
		this.destinationId = destinationId;
		this.sourceId = sourceId;
		this.payload = payload;
		this.nodeTrace = nodeTrace;
	}

	public OverlayNodeSendsData(DataInputStream dataIn) throws IOException {
		destinationId = dataIn.readInt();
		sourceId = dataIn.readInt();
		payload = dataIn.readInt();
		nodeTrace = new int[dataIn.readInt()];
		for(int i=0; i<nodeTrace.length; ++i)
			nodeTrace[i] = dataIn.readInt();
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dataOut.writeByte(Protocol.OVERLAY_NODE_SENDS_DATA);
		dataOut.writeInt(destinationId);
		dataOut.writeInt(sourceId);
		dataOut.writeInt(payload);
		dataOut.writeInt(nodeTrace.length);
		for(int id: nodeTrace)
			dataOut.writeInt(id);

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
