package sdm.time.project6.timeStamps;

import java.util.* ;

import simsim.core.*;
import sdm.time.project6.timeStamps.msgs.*;
import sdm.time.project6.timeStamps.clocks.*;

public class Middleware extends Node implements MiddlewareMessageHandler {
	
	int myFifo_SeqN = 0 ;
	//Map<EndPoint, FifoQueue> fifoQueues = new HashMap<EndPoint, FifoQueue>() ;
	FifoQueue queueQueue = new FifoQueue() ;
	PhysicalClock pClock = new PhysicalClock(this);
	
	
	protected void R_multicast( final Message m ) {
		endpoint.broadcast( m ) ;		
	}
	
	protected void FO_multicast( final Message m ) {
		//endpoint.broadcast( new FifoOrderMulticast( myFifo_SeqN++, endpoint, m ) ) ;
		endpoint.broadcast( new FifoOrderMulticast( pClock.value(), endpoint, m ) ) ;
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
		srcQueue.put( m.seq.value(), m ) ;
		srcQueue.dispatch() ;
	}
	
	
	private FifoQueue fifoQueue( EndPoint src ) {

		return queueQueue ;
	}
	
	@SuppressWarnings("serial")
	class FifoQueue extends TreeMap<Double, FifoOrderMulticast> {
		void dispatch() {
			while( ! isEmpty()) {
				FifoOrderMulticast m = remove(firstEntry().getKey()) ;
				m.payload.deliverTo( m.src, Middleware.this) ;
			}
		}
	}
}

