package cs455.overlay.transport;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
* Created by Cullen on 1/26/2015.
*/
public class TCPReceiver {
	private Socket socket;
	private DataInputStream dataIn;

	public TCPReceiver(Socket socket) throws IOException {
		this.socket = socket;
		dataIn = new DataInputStream(socket.getInputStream());
	}

	public byte[] receive() throws IOException {
		int dataLength;
		while(socket != null)
	}
	public void run() {

	}
}
