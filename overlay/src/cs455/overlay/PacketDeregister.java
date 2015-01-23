package cs455.overlay;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;

/**
 * Created by Cullen on 1/22/2015.
 */
public class PacketDeregister {

	public InetAddress address;
	public int port;
	public int id;

	public PacketDeregister(InetAddress address, int port, int id) {
		this.address = address;
		this.port = port;
		this.id = id;
	}

    public PacketDeregister(byte[] marshalledBytes) throws IOException {
	    ByteArrayInputStream baInputStream =
		    new ByteArrayInputStream(marshalledBytes);
	    DataInputStream din =
		    new DataInputStream(new BufferedInputStream(baInputStream));

	    byte ipLength = din.readByte();
//	    if (ipLength == 4) {
//		    address = new Inet4Address("", )
//	    }
	    baInputStream.close();
	    din.close();

	}
}
