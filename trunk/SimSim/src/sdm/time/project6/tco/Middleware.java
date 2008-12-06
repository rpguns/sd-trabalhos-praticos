package sdm.time.project6.tco;

import java.util.* ;

import simsim.core.*;
import sdm.time.project6.tco.msgs.*;

public class Middleware extends Node implements MiddlewareMessageHandler {
	
	int myFifo_SeqN = 0 ;
	Map<EndPoint, FifoQueue> fifoQueues = new HashMap<EndPoint, FifoQueue>() ;
	
	
	protected void R_multicast( final Message m ) {
		endpoint.broadcast( m ) ;		
	}
	
	protected void FO_multicast( final Message m ) {
		endpoint.broadcast( new FifoOrderMulticast( myFifo_SeqN++, endpoint, m ) ) ;
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
		srcQueue.put( m.seq, m ) ;
		srcQueue.dispatch() ;
	}
	
	
	private FifoQueue fifoQueue( EndPoint src ) {
		FifoQueue queue = fifoQueues.get( src ) ;
		if( queue == null ) {
			queue = new FifoQueue() ;
			fifoQueues.put( src, queue) ;
		}
		return queue ;
	}
	
	@SuppressWarnings("serial")
	class FifoQueue extends TreeMap<Integer, FifoOrderMulticast> {
		int nextSeqN = 0 ;
		
		void dispatch() {
			int first ;
			while( ! isEmpty() && (first = firstKey()) == nextSeqN ) {
				FifoOrderMulticast m = remove(first) ;
				m.payload.deliverTo( m.src, Middleware.this) ;
				nextSeqN++ ;
			}
		}
	}
}

