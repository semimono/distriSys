package cs455.harvester.wireformats;

import cs455.harvester.Crawler;
import cs455.harvester.Page;
import cs455.harvester.Task;
import cs455.harvester.transport.TCPConnection;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cullen on 1/25/2015.
 */
public class CrawlerOpensConnection implements Event {

	public URL root;

	public CrawlerOpensConnection(URL root) {
		this.root = root;
	}

	public CrawlerOpensConnection(DataInputStream dataIn) throws IOException {
		root = new URL(Protocol.readString(dataIn));
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dataOut.writeByte(Protocol.CRAWLER_OPENS_CONNECTION);
		dataOut.writeByte(root.toString().length());
		dataOut.writeBytes(root.toString());

		dataOut.flush();
		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dataOut.close();
		return marshalledBytes;
	}

	@Override
	public void execute(TCPConnection con) {
		Crawler.get().addConnection(con, root);
		System.out.println("Houston, we have a connection.");
	}
}
