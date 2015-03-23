package cs455.harvester.wireformats;

import cs455.harvester.Crawler;
import cs455.harvester.Page;
import cs455.harvester.Task;
import cs455.harvester.transport.TCPConnection;

import java.io.*;
import java.net.URL;

/**
 * Created by Cullen on 1/25/2015.
 */
public class CrawlerSendsPage implements Event {

	public URL target;
	public URL from;

	public CrawlerSendsPage(URL target, URL from) {
		this.target = target;
		this.from = from;
	}

	public CrawlerSendsPage(DataInputStream dataIn) throws IOException {
		target = new URL(Protocol.readString(dataIn));
		from = new URL(Protocol.readString(dataIn));
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dataOut.writeByte(Protocol.CRAWLER_SENDS_URL);
		dataOut.writeInt(target.toString().length());
		dataOut.writeBytes(target.toString());
		dataOut.writeInt(from.toString().length());
		dataOut.writeBytes(from.toString());

		dataOut.flush();
		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dataOut.close();
		return marshalledBytes;
	}

	@Override
	public void execute(TCPConnection con) {
//		System.out.println("RECEIVED MESSAGE!");
		Page newPage = Crawler.get().addPage(target, 0);
		newPage.addFrom(from);
		if (newPage.explore() && newPage.valid()) {
			try {
				Crawler.get().addTask(new Task(newPage));
			} catch (InterruptedException e1) {}
		}
	}
}
