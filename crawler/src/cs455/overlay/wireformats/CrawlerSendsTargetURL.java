package cs455.overlay.wireformats;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.transport.TCPConnection;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cullen on 1/25/2015.
 */
public abstract class CrawlerSendsTargetURL {

	public int destinationId;
	public int sourceId;

	public List<Integer> nodeTrace;

	protected CrawlerSendsTargetURL(int destinationId, int sourceId) {
		this(destinationId, sourceId, new ArrayList<Integer>());
	}

	protected CrawlerSendsTargetURL(int destinationId, int sourceId, List<Integer> nodeTrace) {
		this.destinationId = destinationId;
		this.sourceId = sourceId;
		this.nodeTrace = nodeTrace;
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
		dataOut.writeInt(nodeTrace.size());
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
		MessagingNode.get().receiveMessage(this);
	}
}
