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

}
