package cs455.harvester.wireformats;

import cs455.overlay.node.Registry;
import cs455.harvester.transport.TCPConnection;

import java.io.*;

/**
 * Created by Cullen on 1/25/2015.
 */
public class NodeReportsOverlaySetupStatus implements Event {

	int status;
	String info;

	public NodeReportsOverlaySetupStatus(int status, String info) {
		this.status = status;
		this.info = info;
	}

	public NodeReportsOverlaySetupStatus(DataInputStream dataIn) throws IOException {
		status = dataIn.readInt();
		byte infoLength = dataIn.readByte();
		byte[] infoArray = new byte[infoLength];
		int readInfoLength = dataIn.read(infoArray);
		if (readInfoLength != infoLength)
			throw new IOException("BAD INFORMATION FIELD LENGTH! Correct: " +infoLength +" Incorrect: " +readInfoLength);
		info = new String(infoArray);
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dataOut.writeByte(Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS);
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
		Registry.get().nodeSetUp(status);
	}
}
