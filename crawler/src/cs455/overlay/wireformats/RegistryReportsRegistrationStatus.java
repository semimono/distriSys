package cs455.overlay.wireformats;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.transport.TCPConnection;

import java.io.*;

/**
 * Created by Cullen on 1/25/2015.
 */
public class RegistryReportsRegistrationStatus implements Event {

	public int status;
	public String info;

	public RegistryReportsRegistrationStatus(int status, String info) {
		this.status = status;
		this.info = info;
	}

	public RegistryReportsRegistrationStatus(DataInputStream dataIn) throws IOException {
		status = dataIn.readInt();
		info = Protocol.readString(dataIn);
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dataOut.writeByte(Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS);
		dataOut.writeInt(status);
		dataOut.writeByte(info.length());
		dataOut.writeBytes(info);

		dataOut.flush();
		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dataOut.close();
		return marshalledBytes;
	}

	@Override
	public void execute(TCPConnection con) {
		if (status < 0) {
			System.err.println(info);
			return;
		}
		System.out.println(info);
		MessagingNode.get().setId(status);
	}
}
