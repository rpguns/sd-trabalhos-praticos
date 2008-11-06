package sdm.epidemic.examples.B;

import java.awt.*;
import java.util.*;

import simsim.core.*;
import simsim.utils.*;
import static simsim.core.Simulation.* ;

import sdm.epidemic.examples.B.msgs.*;

public class Node extends AbstractNode implements ExtendedMessageHandler {
	
	private static final int MAX_SEED_NODES = 4;
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
	
	public void init() {
		setColor( Color.GREEN ) ;
		knownNodes.addAll( NodeDB.randomEndPoints(this, MAX_SEED_NODES));
		
		new PeriodicTask(this, rg.nextDouble(), 1.0) {
			public void run() {
				if (infected)
					udpSend( knownNodes.randomElement(), new InfectNode());
			}
		};
		
		new PeriodicTask(this, rg.nextDouble(), 2.0) {
			public void run() {
				udpSend( knownNodes.randomElement(), new SeedExchange(knownNodes));
			}
		};

	}

	public void onReceive(EndPoint src, InfectNode m) {
		this.infect() ;
	}

	public void onSendFailure( EndPoint dst, Message m ) {
		knownNodes.remove( dst ) ;
	}
	

	public void onReceive(EndPoint src, SeedExchange m) {
		Set<EndPoint> x = new HashSet<EndPoint>( knownNodes);
		x.add(src);
		x.addAll(m.seeds);
		knownNodes = new RandomList<EndPoint>(x);

		while (knownNodes.size() > MAX_SEED_NODES)
			knownNodes.removeRandomElement();
	}
}
