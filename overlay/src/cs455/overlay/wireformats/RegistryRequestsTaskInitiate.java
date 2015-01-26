package cs455.overlay.wireformats;

import java.io.*;

/**
 * Created by Cullen on 1/25/2015.
 */
public class RegistryRequestsTaskInitiate implements Protocol {


	public RegistryRequestsTaskInitiate() {
	}

	public RegistryRequestsTaskInitiate(DataInputStream dataIn) throws IOException {
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dataOut.writeByte(REGISTRY_REQUESTS_TASK_INITIATE);

		dataOut.flush();
		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dataOut.close();
		return marshalledBytes;
	}
}
