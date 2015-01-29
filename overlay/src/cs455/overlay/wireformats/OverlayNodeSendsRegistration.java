package cs455.overlay.wireformats;

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
}
