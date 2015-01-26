package cs455.overlay.wireformats;

import java.io.*;

/**
 * Created by Cullen on 1/25/2015.
 */
public class OverlayNodeSendsData implements Protocol {


	public OverlayNodeSendsData() {
	}

	public OverlayNodeSendsData(DataInputStream dataIn) throws IOException {
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dataOut.writeByte(OVERLAY_NODE_SENDS_DATA);

		dataOut.flush();
		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dataOut.close();
		return marshalledBytes;
	}
}
