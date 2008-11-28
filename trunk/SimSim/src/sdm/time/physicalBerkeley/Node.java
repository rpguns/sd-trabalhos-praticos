package sdm.time.physicalBerkeley;

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

//	public void onReceive(EndPoint src, SyncTimeReply m) {
//		//System.out.println("Current time is"+currentTime());
//		double rtt = this.currentTime() - m.timeStamp ;
//		//System.out.println("received time from"+src+" "+(m.referenceTime+rtt/2)+" at "+currentTime()+" with rtt "+rtt);
//			
//		double offset = (m.referenceTime + rtt/2) - this.currentTime() ;
//		
//		childrenTimes.add(new Pair<NetAddress,Double>(src.address,m.referenceTime+rtt/2));
//		
//		if (childrenTimes.size() == children.size()) {
//			//Qual � o tempo que queremos aqui? quando mandamos ou quando recebemos?
//			double avg = m.timeStamp+rtt;
//			System.out.println("timestamprtt "+(m.timeStamp+rtt)+" tempo real "+currentTime());
//			for (Pair<NetAddress,Double> x:childrenTimes)
//				avg += x.getSecond();
//			avg /= (children.size()+1);
//			
//			//System.out.println("average was "+avg+" time is "+currentTime());
//			
//			for (Pair<NetAddress,Double> child:childrenTimes)
//				udpSend(child.getFirst(),new OffsetMessage(avg-child.getSecond()));
//			childrenTimes = new LinkedList<Pair<NetAddress,Double>>();
//			}
//			//udpSend(src,new OffsetMessage((m.timeStamp+rtt/2)-currentTime()));
//		}
	
	
	public void onReceive(EndPoint src, SyncTimeReply m) {
	//System.out.println("Current time is"+currentTime());
	double rtt = this.currentTime() - m.timeStamp ;
	//System.out.println("received time from"+src+" "+(m.referenceTime+rtt/2)+" at "+currentTime()+" with rtt "+rtt);
		
	double offset = (m.referenceTime + rtt/2) - this.currentTime() ;

	
	childrenTimes.add(new Pair<NetAddress,Double>(src.address,offset));
	
	if (childrenTimes.size() == children.size()) {
		//Qual � o tempo que queremos aqui? quando mandamos ou quando recebemos?
		double avg = 0;//+rtt/2;
		
		//System.out.println("timestamprtt "+(m.timeStamp+rtt)+" tempo real "+currentTime());
		for (Pair<NetAddress,Double> x:childrenTimes)
			avg += x.getSecond();
		avg /= (children.size()+1);
		
		//System.out.println("average was "+avg+" time is "+currentTime());
		
		for (Pair<NetAddress,Double> child:childrenTimes) 
			udpSend(child.getFirst(),new OffsetMessage(avg-child.getSecond()));
		super.clock.adjustClock(avg);
		childrenTimes = new LinkedList<Pair<NetAddress,Double>>();
		}
	
		//System.out.println("Child had "+m.referenceTime+" at moment "+(m.timeStamp+rtt/2)+" sending offset: "+offset);
		//udpSend(src,new OffsetMessage(offset));
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
