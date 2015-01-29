package cs455.overlay.wireformats;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by Cullen on 1/22/2015.
 */
public class Protocol {

	public static final byte NODE_REPORTS_OVERLAY_SETUP_STATUS = 0;
	public static final byte OVERLAY_NODE_REPORTS_TASK_FINISHED = 1;
	public static final byte OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY = 2;
	public static final byte OVERLAY_NODE_SENDS_DATA = 3;
	public static final byte OVERLAY_NODE_SENDS_DEREGISTRATION = 4;
	public static final byte OVERLAY_NODE_SENDS_REGISTRATION = 5;
	public static final byte REGISTRY_REPORTS_DEREGISTRATION_STATUS = 6;
	public static final byte REGISTRY_REPORTS_REGISTRATION_STATUS = 7;
	public static final byte REGISTRY_REQUESTS_TASK_INITIATE = 8;
	public static final byte REGISTRY_REQUESTS_TRAFFIC_SUMMARY = 9;
	public static final byte REGISTRY_SENDS_NODE_MANIFEST = 10;

	public static InetAddress readAddress(DataInputStream dataIn) throws IOException {
		byte ipLength = dataIn.readByte();
		byte[] addressArray = new byte[ipLength];
		int readIpLength = dataIn.read(addressArray);
		if (readIpLength != ipLength)
			throw new IOException("BAD IP ADDRESS FIELD LENGTH! Correct: " +ipLength +" Incorrect: " +readIpLength);
		return InetAddress.getByAddress(addressArray);
	}

	public static String readString(DataInputStream dataIn) throws IOException {
		byte infoLength = dataIn.readByte();
		byte[] infoArray = new byte[infoLength];
		int readInfoLength = dataIn.read(infoArray);
		if (readInfoLength != infoLength)
			throw new IOException("BAD STRING FIELD LENGTH! Correct: " +infoLength +" Incorrect: " +readInfoLength);
		return new String(infoArray);
	}
}
