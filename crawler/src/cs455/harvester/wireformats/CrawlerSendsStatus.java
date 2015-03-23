package cs455.harvester.wireformats;

import cs455.harvester.Crawler;
import cs455.harvester.RemoteCrawler;
import cs455.harvester.transport.TCPConnection;

import java.io.*;
import java.net.URL;

/**
 * Created by Cullen on 1/25/2015.
 */
public class CrawlerSendsStatus implements Event {

	public URL root;
	public boolean finished;

	public CrawlerSendsStatus(URL root, boolean finished) {
		this.root = root;
		this.finished = finished;
	}

	public CrawlerSendsStatus(DataInputStream dataIn) throws IOException {
		finished = dataIn.readBoolean();
		root = new URL(Protocol.readString(dataIn));
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dataOut.writeByte(Protocol.CRAWLER_SENDS_STATUS);
		dataOut.writeBoolean(finished);
		dataOut.writeInt(root.toString().length());
		dataOut.writeBytes(root.toString());

		dataOut.flush();
		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dataOut.close();
		return marshalledBytes;
	}

	@Override
	public void execute(TCPConnection con) {
		if (!finished)
			Crawler.get().setIncomplete();
		for(RemoteCrawler c: Crawler.get().getSiblings()) {
			if (c.getRoot().getHost().equals(root.getHost())) {
				c.finished = finished;
				break;
			}
		}
	}
}
