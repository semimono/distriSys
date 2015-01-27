package cs455.overlay.wireformats;

import java.io.IOException;

/**
 * Created by Cullen on 1/22/2015.
 */
public interface Event {

	public byte[] getBytes() throws IOException;
}
