package sdm.time.physicalCristianOffset;

import java.awt.*;

import simsim.core.*;
import static simsim.core.Simulation.*;
import java.util.*;

import sdm.time.physicalBerkeley.msgs.*;

public class Node extends AbstractNode implements ExtendedMessageHandler, Displayable {

	public int index;
	private NetAddress parent ;
	private Set<NetAddress> children;
	private LinkedList<Pair<NetAddress,Double>> childrenTimes;
	private boolean isRoot = false;

	public Node() {
		super( true ); // This node will have its own clock, with drift and skew...
		index = NodeDB.store(this);
		super.setColor( Color.getHSBColor(0.2f, 0.3f, 0.9f)) ;
		childrenTimes = new LinkedList<Pair<NetAddress,Double>>();
	}

	public String toString() {
		return "" + index ;
	}

	public void init( NetAddress rootNode) {
		if( rootNode == this.address ) {
			isRoot = true;
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
				//mesmo currentTime ou meto currentTime() na mensagem?
				double currTime = currentTime();
				for (NetAddress child:children)
					udpSend( child, new SyncTimeRequest(currentTime()));
			}
		};		
	}
	
	public void onReceive(EndPoint src, SyncTimeRequest m) {
		udpSend(src, new SyncTimeReply( m.timeStamp, this.currentTime()));
	}

	
	public void onReceive(EndPoint src, SyncTimeReply m) {
		double rtt = this.currentTime() - m.timeStamp ;
		
		double offset = (m.referenceTime + rtt/2) - this.currentTime() ;
		
		udpSend(src,new OffsetMessage(-offset));
	}
	
	public void onReceive(EndPoint src, OffsetMessage m) {
		//System.out.println(m.offset);
		//System.out.println("Offset Adjustment Received at Node = "+this.index);
		//System.out.println("Current Time = "+super.currentTime()+" | Offset Received = "+m.offset);
		super.clock.adjustClock(m.offset); 
		//System.out.println("New Time = "+super.currentTime()+"\n");
	}
	
	public void display(Graphics2D gu, Graphics2D gs) {
		address.display(gu, gs) ;
		clock.display(gu, gs) ;	
	}
	
}
