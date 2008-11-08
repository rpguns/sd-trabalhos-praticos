package sdm.overlays.project4.wordSearch;

import java.util.*;

import simsim.core.*;
import simsim.utils.*;
import static simsim.core.Simulation.*;

class NodeDB {
	static final int MAX_KEY_LENGTH = 48; // 48 bits to fit in the mantissa of a double...

	static RandomList<Node> nodes = new RandomList<Node>();
	static TreeMap<Long, Node> k2n = new TreeMap<Long, Node>();

	static long store(Node n) {
		for (;;) {
			long key = rg.nextLong() & ((1L << MAX_KEY_LENGTH) - 1L);
			if (!k2n.containsKey(key)) {
				k2n.put(key, n);
				nodes.add(n);
				return key;
			}
		}
	}
	
	Set<Long> keys() {
		return k2n.keySet();
	}

	static void dispose(Node n) {
		if (n != null) {
			k2n.remove(n.key);
			seeds.remove(n);
			nodes.remove(n);
			n.dispose();
		}
	}

	static int size() {
		return k2n.size();
	}

	synchronized static Collection<Node> nodes() {
		return nodes;
	}

	static Node randomNode() {
		return nodes.randomElement();
	}

	static RandomList<Node> seeds = new RandomList<Node>();

	static Collection<EndPoint> randomEndPoints(Node caller, int total) {

		Set<EndPoint> res = new HashSet<EndPoint>();

		if (seeds.isEmpty())
			res.add(nodes.randomElement().endpoint);
		else
			while (res.size() < Math.min(total, seeds.size())) {
				res.add(seeds.randomElement().endpoint);
			}

		seeds.add(caller);
		return res;
	}

}