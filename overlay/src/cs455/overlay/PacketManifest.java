package cs455.overlay;

import cs455.overlay.NodeInfo;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cullen on 1/22/2015.
 */
public class PacketManifest {

	public List<NodeInfo> routingTable;
	public List<int> nodeIds;


	public PacketManifest() {
		routingTable = new ArrayList<NodeInfo>();
		nodeIds = new ArrayList<int>();
	}

	public PacketManifest(ByteArrayInputStream stream) {
		this();

		int ipLength = stream.read();

	}
}
