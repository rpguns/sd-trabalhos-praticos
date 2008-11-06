package sdm.epidemic.examples.C;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;


import sdm.epidemic.examples.C.msgs.*;
import simsim.core.*;
import simsim.utils.*;


public class Node extends AbstractNode implements ExtendedMessageHandler {
	
	private static final int MAX_SEED_NODES = 5;
	private RandomList<EndPoint> knownNodes = new RandomList<EndPoint>();

	boolean infected = false;
	
	public Node() {
		super();
		NodeDB.store( this);
	}


	public void infect() {
		infected = true ;
		setColor( Color.RED ) ;
	}

	/**
	 * The node crashed. Remove it from the DB.
	 */
	public void crash() {
		NodeDB.dispose(this) ;
	}
	
	public void init() {
		setColor( Color.GREEN ) ;
		knownNodes.addAll( NodeDB.randomEndPoints(this, MAX_SEED_NODES));
		
		/**
		 *  This task runs at one second intervals to attempt to infect one of the known nodes.
		 */
		new PeriodicTask(this, 1.0) {
			public void run() {
				if (infected)
					endpoint.udpSend(knownNodes.randomElement(), new InfectNode());
			}
		};
		
		/**
		 *  This task runs at two second intervals to exchange contact endpoint with another node.
		 */
		new PeriodicTask(this, 2.0) {
			public void run() {
					endpoint.udpSend(knownNodes.randomElement(), new SeedExchange(knownNodes));
			}
		};

	}

	public void onReceive(EndPoint src, InfectNode m) {
		this.infect() ;
	}

	/* (non-Javadoc)
	 * 
	 * A message was sent to a node that is offline.
	 * Forget the failed node and free its slot. 
	 * @see simsim.core.AbstractNode#onSendFailure(simsim.core.EndPoint, simsim.core.Message)
	 */
	public void onSendFailure( EndPoint dst, Message m ) {
		knownNodes.remove( dst ) ;
	}
	
	
	
	/* (non-Javadoc)
	 * 
	 * The node received a set of contacts from another node.
	 * Add them to the set of knownodes and trim the list to keep it to the allowed size.
	 * 
	 * @see sdm.epidemic.examples.C.msgs.ExtendedMessageHandler#onReceive(simsim.core.EndPoint, sdm.epidemic.examples.C.msgs.SeedExchange)
	 */
	public void onReceive(EndPoint src, SeedExchange m) {
		Set<EndPoint> x = new HashSet<EndPoint>( knownNodes);
		x.add(src);
		x.addAll(m.seeds);
		knownNodes = new RandomList<EndPoint>(x);

		while (knownNodes.size() > MAX_SEED_NODES)
			knownNodes.removeRandomElement();
	}
}
