package cs455.overlay;

import cs455.overlay.node.Node;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cullen on 1/22/2015.
 */
public class PacketManifest {

	public List<Node> routingTable;
	public List<Integer> nodeIds;


	public PacketManifest() {
		routingTable = new ArrayList<Node>();
		nodeIds = new ArrayList<Integer>();
	}

	public PacketManifest(ByteArrayInputStream stream) {
		this();

		int ipLength = stream.read();

	}
}
