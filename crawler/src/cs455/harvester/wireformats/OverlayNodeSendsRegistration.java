package cs455.harvester.wireformats;

import cs455.overlay.node.Registry;
import cs455.harvester.transport.TCPConnection;

import java.io.*;
import java.net.InetAddress;

/**
 * Created by Cullen on 1/25/2015.
 */
public class OverlayNodeSendsRegistration implements Event {

    public InetAddress address;
    public int port;

    public OverlayNodeSendsRegistration(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public OverlayNodeSendsRegistration(DataInputStream dataIn) throws IOException {
		address = Protocol.readAddress(dataIn);
		port = dataIn.readInt();
    }

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dataOut.writeByte(Protocol.OVERLAY_NODE_SENDS_REGISTRATION);
		dataOut.writeByte(address.getAddress().length);
		dataOut.write(address.getAddress());
		dataOut.writeInt(port);

		dataOut.flush();
		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dataOut.close();
		return marshalledBytes;
	}

	@Override
	public void execute(TCPConnection con) {
		Registry registry = Registry.get();
		int id = -1;
		String info;
		if (!con.getAddress().equals(address)) {
			info = "Error: Incorrect host address specified in registration request";
		} else if (registry.nodeCount() >= Registry.MAXIMUM_MASSAGING_NODES) {
			info = "Error: No more space available in registry";
		} else {
			id = registry.register(address, port, con).id;
			if (id < 0)
				info = "Error: Node already registered at address " +address +":" +port;
			else
				info = "Successfully registered with ID " +id +". The current number of messaging nodes is " +registry.nodeCount();
		}
//		System.out.println("Howdy all!  We've got a message from " +address +":" +port +" or " +con.getAddress() +":" +con.getPort() +"\nthe response is: " +info);
		try {
			con.send(new RegistryReportsRegistrationStatus(id, info));
		} catch (IOException e) {
			if (id > -1) {
				try {
					registry.deregister(id);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			System.err.println("Could not send registration response");
			e.printStackTrace();
		}
	}
}
