package cs455.overlay.wireformats;

import cs455.overlay.Misc;

import java.io.*;
import java.net.InetAddress;

/**
 * Created by Cullen on 1/25/2015.
 */
public class RegistryReportsRegistrationStatus implements Protocol {

	public int status;
	public String info;

	public RegistryReportsRegistrationStatus(int status, String info) {
		this.status = status;
		this.info = info;
	}

	public RegistryReportsRegistrationStatus(DataInputStream dataIn) throws IOException {
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

		dataOut.writeByte(REGISTRY_REPORTS_REGISTRATION_STATUS);
		dataOut.writeInt(status);
		dataOut.writeByte(info.length());
		dataOut.writeBytes(info);

		dataOut.flush();
		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dataOut.close();
		return marshalledBytes;
	}
}
