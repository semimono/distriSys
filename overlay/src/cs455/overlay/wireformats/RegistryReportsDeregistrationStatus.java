package cs455.overlay.wireformats;

import java.io.*;

/**
 * Created by Cullen on 1/25/2015.
 */
public class RegistryReportsDeregistrationStatus implements Event {

	public String info;

	public RegistryReportsDeregistrationStatus(String info) {
		this.info = info;
	}

	public RegistryReportsDeregistrationStatus(DataInputStream dataIn) throws IOException {
		info = Protocol.readString(dataIn);
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dataOut.writeByte(Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS);
		dataOut.writeByte(info.length());
		dataOut.writeBytes(info);

		dataOut.flush();
		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dataOut.close();
		return marshalledBytes;
	}
}
