package sdm.epidemic.examples.C;

import java.util.*;

import simsim.core.*;
import simsim.utils.*;


class NodeDB {
	static RandomList<Node> nodes = new RandomList<Node>() ;

	static void store( Node n ) {
		nodes.add( n ) ;
	}

	static void dispose( Node n ) {
		if( n != null ) {
			seeds.remove( n ) ;
			nodes.remove( n ) ;
			n.dispose();
		}
	}
	
	static int size() {
		return nodes.size();
	}
			
	static Node randomNode() {
		return nodes.randomElement() ;
	}
	
	static Collection<Node> nodes() {
		return nodes ;
	}
	
	// Returns a set of seeds that form connected graph in a transitive sense.
	static RandomList<Node> seeds = new RandomList<Node>() ;
	static Collection<EndPoint> randomEndPoints( Node caller, int total ) {

		Set<EndPoint> res = new HashSet<EndPoint>() ;
		
		if( seeds.isEmpty() )
			res.add( nodes.randomElement().endpoint ) ;			
		else
			while( res.size() < Math.min(total, seeds.size() ) ) {
				res.add( seeds.randomElement().endpoint ) ;
			}
		
		seeds.add( caller ) ;	
		return res ;
	}
}