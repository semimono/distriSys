package cs455.harvester.wireformats;

import cs455.overlay.node.MessagingNode;
import cs455.harvester.transport.TCPConnection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cullen on 1/25/2015.
 */
public abstract class OverlayMessage implements Event {

	public int destinationId;
	public int sourceId;

	public List<Integer> nodeTrace;

	protected OverlayMessage() {
		this(-1, -1);
	}

	protected OverlayMessage(int destinationId, int sourceId) {
		this(destinationId, sourceId, new ArrayList<Integer>());
	}

	protected OverlayMessage(int destinationId, int sourceId, List<Integer> nodeTrace) {
		this.destinationId = destinationId;
		this.sourceId = sourceId;
		this.nodeTrace = nodeTrace;
	}

	@Override
	public void execute(TCPConnection connection) {
		MessagingNode.get().receiveMessage(this);
	}

	public abstract void perform();
}
