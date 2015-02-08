package cs455.overlay.transport;

import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by Cullen on 1/25/2015.
 */
public class TCPConnection extends Thread {

	private TCPSender sender;
	private TCPReceiver receiver;
	private Socket socket;

	public TCPConnection(Socket socket) throws IOException {
		super("TCPConnection");
		this.socket = socket;
		sender = new TCPSender(socket);
		receiver = new TCPReceiver(socket);
		TCPConnectionsCache.get().add(this);
		start();
	}

	@Override
	public void run() {
		System.out.println("starting TCP receiving");
		while(!socket.isClosed()) {
			try {
				receive();
				System.out.println("Received TCP message");
			} catch (IOException e) {
				System.err.println("Failed to receive message");
				e.printStackTrace();
			}
		}
	}

	public void close() throws IOException {
		interrupt();
		socket.close();
		TCPConnectionsCache.get().remove(this);
	}

	public TCPSender getSender() {
		return sender;
	}
	public TCPReceiver getReceiver() {
		return receiver;
	}

	public void send(Event event) throws IOException {
		sender.sendData(event.getBytes());
	}

	public InetAddress getAddress() {
		return socket.getInetAddress();
	}
	public int getPort() {
		return socket.getPort();
	}

	public InetAddress getLocalAddress() {
		return socket.getLocalAddress();
	}
	public int getLocalPort() {
		return socket.getLocalPort();
	}


	private void receive() throws IOException {
		byte[] message;
		message = receiver.receive();

		ByteArrayInputStream baInputStream = new ByteArrayInputStream(message);
		DataInputStream dataIn = new DataInputStream(new BufferedInputStream(baInputStream));
		Event event = EventFactory.get().getEvent(dataIn);
		baInputStream.close();
		dataIn.close();

		if (event != null)
			event.execute(this);
	}

	/**
	* Created by Cullen on 1/26/2015.
	*/
	public static class TCPSender {
		private Socket socket;
		private DataOutputStream dout;

		public TCPSender(Socket socket) throws IOException {
			this.socket = socket;
			dout = new DataOutputStream(socket.getOutputStream());
		}
		public synchronized void sendData(byte[] dataToSend) throws IOException {
			int dataLength = dataToSend.length;
			dout.writeInt(dataLength);
			dout.write(dataToSend, 0, dataLength);
			dout.flush();
		}
	}

	/**
	* Created by Cullen on 1/26/2015.
	*/
	public static class TCPReceiver {
		private Socket socket;
		private DataInputStream dataIn;

		public TCPReceiver(Socket socket) throws IOException {
			this.socket = socket;
			dataIn = new DataInputStream(socket.getInputStream());
		}

		public byte[] receive() throws IOException {
			int dataLength;
			while(!socket.isClosed()) {
				try {
					dataLength = dataIn.readInt();
					byte[] data = new byte[dataLength];
					dataIn.readFully(data, 0, dataLength);
					return data;
				} catch (SocketException e) {

				}
			}
			return null;
		}
	}
}
