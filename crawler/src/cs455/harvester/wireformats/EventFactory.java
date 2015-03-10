package cs455.harvester.wireformats;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.node.Registry;

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
			case Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED:
				return new OverlayNodeReportsTaskFinished(dataIn);
			case Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY:
				return new OverlayNodeReportsTrafficSummary(dataIn);
			case Protocol.OVERLAY_NODE_SENDS_DATA:
				return new OverlayNodeSendsData(dataIn);
			case Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION:
				return new OverlayNodeSendsDeregistration(dataIn);
			case Protocol.OVERLAY_NODE_SENDS_REGISTRATION:
				return new OverlayNodeSendsRegistration(dataIn);

			case Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS:
				return new NodeReportsOverlaySetupStatus(dataIn);
			case Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS:
				return new RegistryReportsDeregistrationStatus(dataIn);
			case Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS:
				return new RegistryReportsRegistrationStatus(dataIn);
			case Protocol.REGISTRY_REQUESTS_TASK_INITIATE:
				return new RegistryRequestsTaskInitiate(dataIn);
			case Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY:
				return new RegistryRequestsTrafficSummary(dataIn);
			case Protocol.REGISTRY_SENDS_NODE_MANIFEST:
				return new RegistrySendsNodeManifest(dataIn);

			case Protocol.CRAWLER_SENDS_URL:
				return new CrawlerSendsPage(dataIn);
			default:
				System.err.println("Unknown Message Type Received: " + messageType);
				return null;
		}
	}

	private Registry registry() {
		Registry reg = Registry.get();
		if (reg == null)
			throw new RuntimeException("MessagingNode received message for Registry");
		return reg;
	}

	private MessagingNode node() {
		MessagingNode node = MessagingNode.get();
		if (node == null)
			throw new RuntimeException("Registry received message for MessagingNode");
		return node;
	}
}
