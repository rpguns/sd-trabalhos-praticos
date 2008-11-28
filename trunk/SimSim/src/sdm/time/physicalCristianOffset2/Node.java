package sdm.time.physicalCristianOffset2;

import java.awt.*;

import simsim.core.*;
import static simsim.core.Simulation.*;
import java.util.*;

import sdm.time.physicalCristianOffset2.msg.*;

public class Node extends AbstractNode implements ExtendedMessageHandler, Displayable {


	public static final int N_OF_TRIES = 3 ;

	
	public int index;
	private NetAddress parent ;
	private Set<NetAddress> children;
	//private LinkedList<Pair<NetAddress,Double>> childrenTimes;
	private HashMap<EndPoint,LinkedList<Pair<Double,Double>>> childrenTimes;

	public Node() {
		super( true ); // This node will have its own clock, with drift and skew...
		index = NodeDB.store(this);
		super.setColor( Color.getHSBColor(0.2f, 0.3f, 0.9f)) ;
		childrenTimes = new HashMap<EndPoint,LinkedList<Pair<Double,Double>>>();//new LinkedList<Pair<NetAddress,Double>>();
	}

	public String toString() {
		return "" + index ;
	}

	public void init( NetAddress rootNode) {
		if( rootNode == this.address ) {
			clock.isMasterClock( true );
			super.setColor( Color.getHSBColor(0.0f, 0.5f, 0.8f)) ;	
		}
		
		// Dado o nó com o relógio de referência, que será raiz da árvore de sincronização, obtém o pai do nó corrente nessa árvore.
		parent = Spanner.parent( rootNode, this.address) ;
	
		children = Spanner.children(rootNode, this.address);
		
		
		//if( parent != null )
			initClockSynchronizationTask() ;
	}
	
//--------------------------------------------------------------------------------------------------------------
	// Tarefa para a sincronização dos relógios...
	// Usa algoritmo de Cristian numa árvore.
	// i.e., Cada nó, periodicamente, pede o valor do relógio ao pai, calcula o desvio com a resposta e faz o acerto no seu relógio.
	private void initClockSynchronizationTask() {		
		new PeriodicTask(this, 10*rg.nextDouble(), 5.0) {
			public void run() {
				double currTime = currentTime();
				for (NetAddress child:children) {
					for (int i = 0; i < N_OF_TRIES; i++)
						udpSend( child, new SyncTimeRequest(currentTime()));
				}
			}
		};		
	}
	
	public void onReceive(EndPoint src, SyncTimeRequest m) {
		udpSend(src, new SyncTimeReply( m.timeStamp, this.currentTime()));
	}

	
	public void onReceive(EndPoint src, SyncTimeReply m) {
		
		if (childrenTimes.get(src) == null) childrenTimes.put(src, new LinkedList<Pair<Double,Double>>());
		double rtt = this.currentTime() - m.timeStamp ;
		
		double offset = (m.referenceTime + rtt/2) - this.currentTime() ;
		
		LinkedList<Pair<Double,Double>> rtts = childrenTimes.get(src);
		
		
		rtts.add(new Pair<Double,Double>(rtt,offset));
		
		if (rtts.size() == N_OF_TRIES) {
			Pair<Double,Double> best = new Pair<Double,Double>(rtt,offset);
			for (Pair<Double,Double> x:rtts) {
				System.out.println(x.getFirst()+" < "+best.getFirst());
				if (x.getFirst() < best.getFirst())
					best = x;
			}
			System.out.println("Went with"+best.getFirst()+" "+best.getSecond());
			udpSend(src,new OffsetMessage(-best.getSecond()));
			childrenTimes.put(src,new LinkedList<Pair<Double,Double>>());
		}
	}
	
	public void onReceive(EndPoint src, OffsetMessage m) {
		System.out.println("Received offset "+m.offset);
		super.clock.adjustClock(m.offset); 
	}
	
	public void display(Graphics2D gu, Graphics2D gs) {
		address.display(gu, gs) ;
		clock.display(gu, gs) ;	
	}
	
}
