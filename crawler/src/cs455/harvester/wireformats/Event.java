package cs455.harvester.wireformats;

import cs455.harvester.transport.TCPConnection;

import java.io.IOException;

/**
 * Created by Cullen on 1/22/2015.
 */
public interface Event {

	public byte[] getBytes() throws IOException;

	public void execute(TCPConnection con);

}
