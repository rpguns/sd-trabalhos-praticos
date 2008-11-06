package sdm.overlays.project3.wordSearchMultiple;

import java.util.*;

import simsim.core.*;
import simsim.utils.*;

class NodeDB {
	static final int MAX_KEY_LENGTH = 63; 
	
	static RandomList<Node> nodes = new RandomList<Node>();

	static void store(Node n) {
		nodes.add( n ) ;
	}

	static Collection<Node> nodes() {
		return nodes;
	}

	static Node randomNode() {
		return nodes.randomElement();
	}

	static RandomList<Node> seeds = new RandomList<Node>();
	static RandomList<EndPoint> randomEndPoints(Node caller, int total) {
		
		while( seeds.size() < total) {
			Node x = NodeDB.randomNode() ;
			if( ! seeds.contains( x ) )
				seeds.add( x ) ;
		}
		
		Set<EndPoint> res = new HashSet<EndPoint>();
		while ( res.size() < total ) {
			Node x = seeds.randomElement() ;
			if( x != caller )
				res.add( x.endpoint );
		}

		if( ! seeds.contains(caller) )
			seeds.add(caller);

		return new RandomList<EndPoint>(res);
	}

	//Obtém os contactos mais próximos em termos de latência. Só usar para fazer debug visual dos algoritmos.
	static RandomList<EndPoint> closestNodes(final Node center, int total) {
		TreeSet<EndPoint> sl = new TreeSet<EndPoint>( new Comparator<EndPoint>() {
			public int compare(EndPoint a, EndPoint b) {
				double da = center.endpoint.latency( a);
				double db = center.endpoint.latency( b);
				if (da == db) {
					return a.hashCode() < b.hashCode() ? -1 : 1;
				}
				else return da < db ? -1 : 1;
			}
		}
		);
		
		for (Node i : nodes())
			sl.add(i.endpoint);

		RandomList<EndPoint> res = new RandomList<EndPoint>();
		for (EndPoint i : sl) {
			res.add(i);
			if (total-- == 0)
				break;
		}
		return res;
	}
}