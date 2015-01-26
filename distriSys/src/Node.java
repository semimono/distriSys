
import context

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Cullen on 1/21/2015.
 */
public class Node {

    public int id;

    public Node() throws UnknownHostException {
        initializeSocket();
        register();
    }

    private void initializeSocket() throws UnknownHostException {
        InetAddress.getLocalHost();

        Socket socket = new Socket();
    }

    private void register() {

    }
}
