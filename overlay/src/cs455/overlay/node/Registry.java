package cs455.overlay.node;

import cs455.overlay.wireformats.*;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Created by Cullen on 1/22/2015.
 */
public class Registry {

	List<NodeInfo> nodes;
	Random rand;

	public Registry() {
		rand = new Random();
	}

	private void receivePacket(byte[] packet) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(packet);
		DataInputStream dataIn = new DataInputStream(new BufferedInputStream(baInputStream));

		byte messageType = dataIn.readByte();
		switch(messageType) {
			case Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED: {
				OverlayNodeReportsTaskFinished message = new OverlayNodeReportsTaskFinished(dataIn);
				break;
			}
			case Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY: {
				OverlayNodeReportsTrafficSummary message = new OverlayNodeReportsTrafficSummary(dataIn);
				break;
			}
			case Protocol.OVERLAY_NODE_SENDS_DATA: {
				OverlayNodeSendsData message = new OverlayNodeSendsData(dataIn);
				break;
			}
			case Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION: {
				OverlayNodeSendsDeregistration message = new OverlayNodeSendsDeregistration(dataIn);
				break;
			}
			case Protocol.OVERLAY_NODE_SENDS_REGISTRATION: {
				OverlayNodeSendsRegistration message = new OverlayNodeSendsRegistration(dataIn);
				break;
			}
			default:
				System.err.println("Unknown Message Type Received: " + messageType);
		}

		baInputStream.close();
		dataIn.close();
	}

	public void register(NodeInfo node) {
		boolean match;
		do {
			node.id = rand.nextInt(128);
			match = false;
			for(NodeInfo n: nodes) {
				if (n.id == node.id) {
					match = true;
					break;
				}
			}
		} while(match);
		nodes.add(node);
	}

	public void unregister(NodeInfo node) {
		nodes.remove(node);
	}

	public static void main(String[] args) {

	}
}
