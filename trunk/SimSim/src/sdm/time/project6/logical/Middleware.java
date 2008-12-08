package sdm.time.project6.logical;

import java.util.* ;

import simsim.core.*;
import sdm.time.project6.logical.msgs.*;
import sdm.time.project6.logical.clocks.*;

public class Middleware extends Node implements MiddlewareMessageHandler {
	
	int myFifo_SeqN = 0 ;
	//Map<EndPoint, FifoQueue> fifoQueues = new HashMap<EndPoint, FifoQueue>() ;
	FifoQueue queueQueue = new FifoQueue() ;
	VectorClock vClock = new VectorClock(this.index,16);
	
	
	protected void R_multicast( final Message m ) {
		endpoint.broadcast( m ) ;		
	}
	
	protected void FO_multicast( final Message m ) {
		//endpoint.broadcast( new FifoOrderMulticast( myFifo_SeqN++, endpoint, m ) ) ;
		endpoint.broadcast( new FifoOrderMulticast( vClock.increment().value(), endpoint, m ) ) ;
	}
	
	protected void TO_multicast( final Message m ) {
		EndPoint sequencer = NodeDB.k2n.get(0).endpoint ;
		endpoint.udpSend(sequencer, new TotalOrderMulticast( endpoint, m ) ) ; 
	}
	
	public void onReceive(EndPoint src, TotalOrderMulticast m) {
		FO_multicast( m.payload ) ;
	}
	
	public void onReceive(EndPoint src, FifoOrderMulticast m) {
		FifoQueue srcQueue = fifoQueue( src ) ;
		vClock.update(m.seq);
		srcQueue.put( vClock.increment().value(), m ) ;
		srcQueue.dispatch() ;
	}
	
	
	private FifoQueue fifoQueue( EndPoint src ) {
		return queueQueue ;
	}
	
	@SuppressWarnings("serial")
	class FifoQueue extends TreeMap<TimeStamp, FifoOrderMulticast> {
		void dispatch() {
			while( ! isEmpty()) {
				if (index == 0) {
					TimeStamp key = firstEntry().getKey();
					System.out.println(key);
				}
				TimeStamp key = firstEntry().getKey();
				FifoOrderMulticast m = remove(firstEntry().getKey()) ;
				m.payload.deliverTo( m.src, Middleware.this);
			}
		}
	}
}
