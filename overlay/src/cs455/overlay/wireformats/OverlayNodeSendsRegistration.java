package cs455.overlay.wireformats;

import cs455.overlay.Misc;

import java.io.*;
import java.net.InetAddress;

/**
 * Created by Cullen on 1/25/2015.
 */
public class OverlayNodeSendsRegistration implements Protocol {

    public InetAddress address;
    public int port;

    public OverlayNodeSendsRegistration(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public OverlayNodeSendsRegistration(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream dataIn = new DataInputStream(new BufferedInputStream(baInputStream));

		address = Misc.readAddress(dataIn);
		port = dataIn.readInt();

		baInputStream.close();
	    dataIn.close();
    }

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dataOut.writeByte(OVERLAY_NODE_SENDS_REGISTRATION);
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
