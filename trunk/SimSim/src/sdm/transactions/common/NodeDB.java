package sdm.transactions.common;

import java.util.*;


import simsim.utils.*;

public class NodeDB {
	
	public static int store( Node n) {
		nodes.add(n) ;
		int key = k2n.size() ;
		k2n.put( key, n ) ;
		return key ;
	}

	public static void dispose( Node n) {
		if (n != null) {
			k2n.remove(n.key);
			nodes.remove(n);
			n.dispose();
		}
	}

	public static int size() {
		return k2n.size();
	}

	public static Node randomNode() {
		return nodes.randomElement();
	}

	public static Collection<Node> nodes() {
		return nodes ;
	}
	
	private static RandomList<Node> nodes = new RandomList<Node>();
	private static TreeMap<Integer, Node> k2n = new TreeMap<Integer, Node>();
}