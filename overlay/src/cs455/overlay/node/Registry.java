package cs455.overlay.node;

import java.util.List;
import java.util.Random;

/**
 * Created by Cullen on 1/22/2015.
 */
public class Registry {

	List<NodeInfo> nodes;
	Random rand;

	public Registry() {
		rand = new Random();
	}

	public void register(NodeInfo node) {
		boolean match;
		do {
			node.id = rand.nextInt(128);
			match = false;
			for(NodeInfo n: nodes) {
				if (n.id == node.id) {
					match = true;
					break;
				}
			}
		} while(match);
		nodes.add(node);
	}

	public void unregister(NodeInfo node) {
		nodes.remove(node);
	}

	public static void main(String[] args) {

	}
}
