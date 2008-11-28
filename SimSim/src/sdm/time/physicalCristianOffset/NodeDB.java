package sdm.time.physicalCristianOffset;

import java.util.*;

import simsim.utils.*;

class NodeDB {
	static RandomList<Node> nodes = new RandomList<Node>();
	static TreeMap<Integer, Node> k2n = new TreeMap<Integer, Node>();

	synchronized static int store(Node n) {
		nodes.add(n) ;
		int index = k2n.size() ;
		k2n.put( index, n ) ;
		return index ;
	}

	synchronized Set<Integer> keys() {
		return k2n.keySet();
	}

	synchronized static void dispose(Node n) {
		if (n != null) {
			k2n.remove(n.index);
			nodes.remove(n);
			n.dispose();
		}
	}

	static int size() {
		return k2n.size();
	}

	static Node randomNode() {
		return nodes.randomElement();
	}
}
