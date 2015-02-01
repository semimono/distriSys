package cs455.overlay.wireformats;

import cs455.overlay.node.Node;
import cs455.overlay.node.Registry;
import cs455.overlay.transport.TCPConnection;

import java.io.*;
import java.net.InetAddress;

/**
 * Created by Cullen on 1/25/2015.
 */
public class OverlayNodeSendsDeregistration implements Event {

	public InetAddress address;
	public int port;
	public int id;

	public OverlayNodeSendsDeregistration(InetAddress address, int port, int id) {
		this.address = address;
		this.port = port;
		this.id = id;
	}

	public OverlayNodeSendsDeregistration(DataInputStream dataIn) throws IOException {
		address = Protocol.readAddress(dataIn);
		port = dataIn.readInt();
		id = dataIn.readInt();
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dataOut.writeByte(Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION);
		dataOut.writeByte(address.getAddress().length);
		dataOut.write(address.getAddress());
		dataOut.writeInt(port);
		dataOut.writeInt(id);

		dataOut.flush();
		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dataOut.close();
		return marshalledBytes;
	}

	@Override
	public void execute(TCPConnection con) {
		Registry registry = Registry.get();
		Node oldNode = registry.get(id);
		String info;
		if (oldNode == null) {
			info = "Error: Node ID " +id +" for deregistration not present in registry";
		} else if (!address.equals(con.getAddress()) || !oldNode.address.equals(address) || oldNode.port != port) {
			info = "Error: Incorrect host address specified in deregistration request";
		} else {
			registry.deregister(id);
			info = "Deregistration successful for node id: " +id;
		}
		try {
			con.send(new RegistryReportsDeregistrationStatus(info));
		} catch (IOException e) {
			System.err.println("Could not send deregistration response");
			e.printStackTrace();
		}
	}
}
