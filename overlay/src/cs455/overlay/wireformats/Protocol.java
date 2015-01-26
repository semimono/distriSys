package cs455.overlay.wireformats;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by Cullen on 1/22/2015.
 */
public interface Protocol {

	public static final byte OVERLAY_NODE_REPORTS_TASK_FINISHED = 0;
	public static final byte OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY = 1;
	public static final byte OVERLAY_NODE_SENDS_DATA = 2;
	public static final byte OVERLAY_NODE_SENDS_DEREGISTRATION = 3;
	public static final byte OVERLAY_NODE_SENDS_REGISTRATION = 4;
	public static final byte REGISTRY_REPORTS_DEREGISTRATION_STATUS = 5;
	public static final byte REGISTRY_REPORTS_REGISTRATION_STATUS = 6;
	public static final byte REGISTRY_REQUESTS_TASK_INITIATE = 7;
	public static final byte REGISTRY_REQUESTS_TRAFFIC_SUMMARY = 8;
	public static final byte REGISTRY_SENDS_NODE_MANIFEST = 9;

	public byte[] getBytes() throws IOException;

}
