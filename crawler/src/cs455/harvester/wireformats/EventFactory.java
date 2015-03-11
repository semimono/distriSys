package cs455.harvester.wireformats;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Cullen on 1/22/2015.
 */
public class EventFactory {

    private static EventFactory singleton = null;

    public synchronized static EventFactory get() {
        if (singleton == null) {
            singleton = new EventFactory();
        }
        return singleton;
    }

	public Event getEvent(DataInputStream dataIn) throws IOException {
		byte messageType = dataIn.readByte();
		switch(messageType) {
			case Protocol.CRAWLER_SENDS_URL:
				return new CrawlerSendsPage(dataIn);
			case Protocol.CRAWLER_OPENS_CONNECTION:
				return new CrawlerOpensConnection(dataIn);
			case Protocol.CRAWLER_SENDS_STATUS:
				return new CrawlerSendsStatus(dataIn);
			default:
				System.err.println("Unknown Message Type Received: " + messageType);
				return null;
		}
	}
}
