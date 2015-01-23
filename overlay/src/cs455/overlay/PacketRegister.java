package cs455.overlay;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.InetAddress;

/**
 * Created by Cullen on 1/22/2015.
 */
public class PacketRegister {

	public InetAddress address;
	public int port;

	public PacketRegister(InetAddress address, int port) {
		this.address = address;
		this.port = port;
	}

    public PacketRegister(ByteArrayInputStream stream) {

		int ipLength = stream.read();

	}
}
