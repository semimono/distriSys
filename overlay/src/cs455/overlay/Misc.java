package cs455.overlay;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by Cullen on 1/26/2015.
 */
public class Misc {

	public static InetAddress readAddress(DataInputStream din) throws IOException {
		byte ipLength = din.readByte();
		byte[] addressArray = new byte[ipLength];
		int readIpLength = din.read(addressArray);
		if (readIpLength != ipLength)
			throw new IOException("BAD IP ADDRESS FIELD LENGTH! Correct: " +ipLength +" Incorrect: " +readIpLength);
		return InetAddress.getByAddress(addressArray);
	}
}
