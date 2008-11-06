package sdm.epidemic.examples.A;

import java.util.*;

import simsim.utils.*;

/**
 * Maintains the list of existing simulation nodes.
 * 
 * The list of existing nodes is mainly intended to allow an "external observer" to obtain statistical
 * information of the simulation as a whole. For instance, in this example it is used to periodically 
 * monitor the rate of infection in the system.
 * 
 * Be careful how you use global information to implement the behavior of the nodes being simulated.
 * As a rule of thumb, try to avoid it altogether!!!! Otherwise, the solution you are implementing
 * is not truly distributed...
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 *
 */
class NodeDB {
	
	static RandomList<Node> nodes = new RandomList<Node>() ;
	
	static void store( Node n ) {
		if( n != null )
			nodes.add(n) ;
	}

	static int size() {
		return nodes.size();
	}
			
	static Collection<Node> nodes() {
		return nodes ;
	}
	
	static Node randomNode() {
		return nodes.randomElement() ;
	}	
}