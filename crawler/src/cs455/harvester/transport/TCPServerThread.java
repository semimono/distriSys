package cs455.harvester.transport;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Cullen on 1/25/2015.
 */
public class TCPServerThread extends Thread {

	private int port;
	ServerSocket serverSocket;

	public TCPServerThread() throws IOException {
		this(0);
	}
	public TCPServerThread(int port) throws IOException {
		super("TCPServer");
		serverSocket = new ServerSocket(port);
		this.port = serverSocket.getLocalPort();
	}

	public int getPort() {
		return port;
	}

	public void close() throws IOException {
		interrupt();
		serverSocket.close();
	}

	@Override
	public void run() {
		try {
			listen();
		} catch (IOException e) {
			if (!interrupted()) {
				System.err.println("TCP listening stopped erroneously");
				e.printStackTrace();
				return;
			}
		}
		System.out.println("TCP listening stopped");
	}

	public void listen() throws IOException {
		while (!serverSocket.isClosed()) {
			Socket socket = serverSocket.accept();
			new TCPConnection(socket);
			System.out.println("Opened new TCP connection");
		}
	}
}
