package cs455.overlay.wireformats;

import cs455.overlay.routing.RoutingTable;

import java.io.*;

/**
 * Created by Cullen on 1/25/2015.
 */
public class RegistrySendsNodeManifest implements Protocol {

	public RoutingTable table;
	public int[] nodeIds;

	public RegistrySendsNodeManifest(RoutingTable table, int[] nodeIds) {
		this.table = table;
		this.nodeIds = nodeIds;
	}

	public RegistrySendsNodeManifest(DataInputStream dataIn) throws IOException {

	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dataOut.writeByte(REGISTRY_SENDS_NODE_MANIFEST);

		dataOut.flush();
		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dataOut.close();
		return marshalledBytes;
	}
}
