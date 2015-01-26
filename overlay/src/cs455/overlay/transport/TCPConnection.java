package cs455.overlay.transport;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Cullen on 1/25/2015.
 */
public class TCPConnection {

	public class TCPSender {
		private Socket socket;
		private DataOutputStream dout;
		public TCPSender(Socket socket) throws IOException {
			this.socket = socket;
			dout = new DataOutputStream(socket.getOutputStream());
		}
		public void sendData(byte[] dataToSend) throws IOException {
			int dataLength = dataToSend.length;
			dout.writeInt(dataLength);
			dout.write(dataToSend, 0, dataLength);
			dout.flush();
		}
	}
	public class TCPReceiverThread {
		private Socket socket;
		private DataInputStream din;
		public TCPReceiverThread(Socket socket) throws IOException {
			this.socket = socket;
			din = new DataInputStream(socket.getInputStream());
		}
		public void run() {

		}
	}
}
