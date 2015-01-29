package cs455.overlay.transport;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Cullen on 1/25/2015.
 */
public class TCPServerThread {

	public static void listen(int port) throws IOException {
		ServerSocket serverSocket = new ServerSocket(port);
		while (true) {
			Socket socket = serverSocket.accept();
			while (socket.isConnected()) {
				TCPReceiver receiver = new TCPReceiver(socket);
				TCPSender sender = new TCPSender(socket);
				byte[] rawBytes = receiver.receive();
				String message = new String(rawBytes);
				message = message.replaceAll(" ", "_");
				sender.sendData(message.getBytes());
			}
		}
	}


}
