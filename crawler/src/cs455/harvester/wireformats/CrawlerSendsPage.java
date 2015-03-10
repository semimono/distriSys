package cs455.harvester.wireformats;

import cs455.harvester.Crawler;
import cs455.harvester.Page;
import cs455.harvester.Task;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cullen on 1/25/2015.
 */
public class CrawlerSendsPage extends OverlayMessage {

	public URL target;

	public CrawlerSendsPage(int destinationId, int sourceId, URL target) {
		this(destinationId, sourceId, target, new ArrayList<Integer>());
	}

	public CrawlerSendsPage(int destinationId, int sourceId, URL target, List<Integer> nodeTrace) {
		super(destinationId, sourceId, nodeTrace);
		this.target = target;
	}

	public CrawlerSendsPage(DataInputStream dataIn) throws IOException {
		destinationId = dataIn.readInt();
		sourceId = dataIn.readInt();
		target = new URL(Protocol.readString(dataIn));
		int traceSize = dataIn.readInt();
		nodeTrace = new ArrayList<Integer>(traceSize);
		for(int i=0; i<traceSize; ++i)
			nodeTrace.add(dataIn.readInt());
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dataOut.writeByte(Protocol.CRAWLER_SENDS_URL);
		dataOut.writeInt(destinationId);
		dataOut.writeInt(sourceId);
		dataOut.writeByte(target.toString().length());
		dataOut.writeBytes(target.toString());
		dataOut.writeInt(nodeTrace.size());
		for(int id: nodeTrace)
			dataOut.writeInt(id);

		dataOut.flush();
		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dataOut.close();
		return marshalledBytes;
	}

	@Override
	public void perform() {
		Page newPage = Crawler.get().addPage(target, 0);
		if (newPage.explore()) {
			try {
				Crawler.get().addTask(new Task(newPage));
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}
}
