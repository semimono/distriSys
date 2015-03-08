package cs455.overlay.util;

import cs455.overlay.node.Node;
import cs455.overlay.wireformats.OverlayNodeReportsTrafficSummary;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

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
	private SortedMap<Integer, OverlayNodeReportsTrafficSummary> nodes;

	public StatisticsCollectorAndDisplay() {
		fieldLengths = new int[] {header[0].length(),
			header[1].length(),
			header[2].length(),
			header[3].length(),
			header[4].length(),
			header[5].length()};
		nodes = new TreeMap<Integer, OverlayNodeReportsTrafficSummary>();
	}

	public void addStat(OverlayNodeReportsTrafficSummary trafficSummary, int id) {
		setLength(0, s(id).length());
		packetsSent += trafficSummary.packetsSent;
		setLength(1, s(packetsSent).length());
		setLength(1, s(trafficSummary.packetsSent).length());
		packetsReceived += trafficSummary.packetsReceived;
		setLength(2, s(packetsReceived).length());
		setLength(2, s(trafficSummary.packetsReceived).length());
		packetsRelayed += trafficSummary.packetsRelayed;
		setLength(3, s(packetsRelayed).length());
		setLength(3, s(trafficSummary.packetsRelayed).length());
		dataSent += trafficSummary.dataSent;
		setLength(4, s(dataSent).length());
		setLength(4, s(trafficSummary.dataSent).length());
		dataReceived += trafficSummary.dataReceived;
		setLength(5, s(dataReceived).length());
		setLength(5, s(trafficSummary.dataReceived).length());
		nodes.put(id, trafficSummary);
	}

	public String header() {
		String h = "     " +pad(header[0], fieldLengths[0]);
		for(int i=1; i<header.length; ++i)
			h += " | " +pad(header[i], fieldLengths[i]);
		return h;
	}

	public void print() {
		System.out.println(header());
		for(int id: nodes.keySet()) {
			System.out.println(getNode(id));
		}
		System.out.println(summary());
	}

	public String getNode(int node) {
		OverlayNodeReportsTrafficSummary trafficSummary = nodes.get(node);
		if (trafficSummary == null)
			return "";
		return "Node " + pad(s(node), fieldLengths[0]) +" | "
			+printValues(s(trafficSummary.packetsSent),
				s(trafficSummary.packetsReceived),
				s(trafficSummary.packetsRelayed),
				s(trafficSummary.dataSent),
				s(trafficSummary.dataReceived));
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
