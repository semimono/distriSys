package cs455.overlay.node;

import cs455.overlay.wireformats.*;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Cullen on 1/22/2015.
 */
public class MessagingNode {

	private NodeInfo info;

	public MessagingNode() throws UnknownHostException {
		initializeSocket();
		register();
	}

	private void receivePacket(byte[] packet) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(packet);
		DataInputStream dataIn = new DataInputStream(new BufferedInputStream(baInputStream));

		byte messageType = dataIn.readByte();
		switch(messageType) {
			case Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS: {
				RegistryReportsDeregistrationStatus message = new RegistryReportsDeregistrationStatus(dataIn);
				break;
			}
			case Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS: {
				RegistryReportsRegistrationStatus message = new RegistryReportsRegistrationStatus(dataIn);
				break;
			}
			case Protocol.REGISTRY_REQUESTS_TASK_INITIATE: {
				RegistryRequestsTaskInitiate message = new RegistryRequestsTaskInitiate(dataIn);
				break;
			}
			case Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY: {
				RegistryRequestsTrafficSummary message = new RegistryRequestsTrafficSummary(dataIn);
				break;
			}
			case Protocol.REGISTRY_SENDS_NODE_MANIFEST: {
				RegistrySendsNodeManifest message = new RegistrySendsNodeManifest(dataIn);
				break;
			}
			default:
				System.err.println("Unknown Message Type Received: " + messageType);
		}

		baInputStream.close();
		dataIn.close();
	}

	private void initializeSocket() throws UnknownHostException {
		InetAddress.getLocalHost();

		Socket socket = new Socket();
	}

	private void register() {

	}

	public static void main(String[] args) {

	}
}
