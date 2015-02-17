package cs455.overlay.util;

import cs455.overlay.node.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cullen on 1/25/2015.
 */
public class StatisticsCollectorAndDisplay {

	public static final String[] header = new String[] {"", "Sent", "Received", "Relayed", "Values Sent", "Values Received"};

	private int packetsSent;
	private int packetsReceived;
	private int packetsRelayed;
	private long dataSent;
	private long dataReceived;
	private int[] fieldLengths;
	private List<Node> nodes;

	public StatisticsCollectorAndDisplay() {
		fieldLengths = new int[] {header[0].length(),
			header[1].length(),
			header[2].length(),
			header[3].length(),
			header[4].length(),
			header[5].length()};
		nodes = new ArrayList<Node>();
	}

	public void addStat(Node node) {
		setLength(0, s(node.id).length());
		packetsSent += node.trafficSummary.packetsSent;
		setLength(1, s(packetsSent).length());
		packetsReceived += node.trafficSummary.packetsReceived;
		setLength(2, s(packetsReceived).length());
		packetsRelayed += node.trafficSummary.packetsRelayed;
		setLength(3, s(packetsRelayed).length());
		dataSent += node.trafficSummary.dataSent;
		setLength(4, s(dataSent).length());
		dataReceived += node.trafficSummary.dataReceived;
		setLength(5, s(dataReceived).length());
		nodes.add(node);
	}

	public String header() {
		String h = "     " +pad(header[0], fieldLengths[0]);
		for(int i=1; i<header.length; ++i)
			h += " | " +pad(header[i], fieldLengths[i]);
		return h;
	}

	public void print() {
		System.out.println(header());
		for(Node n: nodes) {
			System.out.println(getNode(n));
		}
		System.out.println(summary());
	}

	public String getNode(Node node) {
		return "Node " + pad(s(node.id), fieldLengths[0]) +" | "
			+printValues(s(node.trafficSummary.packetsSent),
				s(node.trafficSummary.packetsReceived),
				s(node.trafficSummary.packetsRelayed),
				s(node.trafficSummary.dataSent),
				s(node.trafficSummary.dataReceived));
	}

	public String summary() {
		return "Sum  " + pad("", fieldLengths[0]) +" | "
			+printValues(s(packetsSent), s(packetsReceived), s(packetsRelayed), s(dataSent), s(dataReceived));
	}

	private String printValues(String packetsSent, String packetsReceived, String packetsRelayed, String dataSent, String dataReceived) {
		return pad(packetsSent, fieldLengths[1])
			+" | " + pad(packetsReceived, fieldLengths[2])
			+" | " + pad(packetsRelayed, fieldLengths[3])
			+" | " + pad(dataSent, fieldLengths[4])
			+" | " + pad(dataReceived, fieldLengths[5]);
	}

	private void setLength(int index, int length) {
		if (fieldLengths[index] < length)
			fieldLengths[index] = length;
	}

	private String pad(String target, int width) {
		return String.format("%" +width +"s", target);
	}

	private String s(long l) {
		return ((Long)l).toString();
	}

}
