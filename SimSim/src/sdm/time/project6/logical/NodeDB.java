package sdm.time.project6.logical;

import java.util.*;

import simsim.utils.*;

class NodeDB {
	static RandomList<Node> nodes = new RandomList<Node>();
	static TreeMap<Integer, Node> k2n = new TreeMap<Integer, Node>();

	static int store(Node n) {
		nodes.add(n) ;
		int index = k2n.size() ;
		k2n.put( index, n ) ;
		return index ;
	}

	Set<Integer> keys() {
		return k2n.keySet();
	}

	static int size() {
		return k2n.size();
	}

	static Node randomNode() {
		return nodes.randomElement();
	}
}
