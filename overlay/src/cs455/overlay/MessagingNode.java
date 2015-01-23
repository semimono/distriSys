package cs455.overlay;

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

	private void initializeSocket() throws UnknownHostException {
		InetAddress.getLocalHost();

		Socket socket = new Socket();
	}

	private void register() {

	}

	public static void main(String[] args) {

	}
}
