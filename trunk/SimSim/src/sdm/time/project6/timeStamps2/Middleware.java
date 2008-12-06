package sdm.time.project6.timeStamps2;

import java.util.* ;

import simsim.core.*;
import sdm.time.project6.timeStamps2.msgs.*;
import sdm.time.project6.timeStamps2.clocks.*;

public class Middleware extends Node implements MiddlewareMessageHandler {
	
	
	//Map<EndPoint, FifoQueue> fifoQueues = new HashMap<EndPoint, FifoQueue>() ;
	FifoQueue queueQueue = new FifoQueue() ;
	PhysicalClock pClock = new PhysicalClock(this);
	double nextSendingTime = pClock.value().value() ;
	
	protected void R_multicast( final Message m ) {
		endpoint.broadcast( m ) ;		
	}
	
	protected void FO_multicast( final Message m ) {
		//endpoint.broadcast( new FifoOrderMulticast( myFifo_SeqN++, endpoint, m ) ) ;
		TimeStamp t = pClock.value();
		//t.delay(0.2);
		
		endpoint.broadcast( new FifoOrderMulticast( t , endpoint, m ) ) ;
		
			
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
//		FifoQueue queue = fifoQueues.get( src ) ;
//		if( queue == null ) {
//			queue = new FifoQueue() ;
//			fifoQueues.put( src, queue) ;
//		}
		return queueQueue ;
	}
	
	@SuppressWarnings("serial")
	class FifoQueue extends TreeMap<Double, FifoOrderMulticast> {
		void dispatch() {
			double first;
			if (!isEmpty())
				System.out.println("Attempting to dispatch message with time = "+firstEntry().getKey()+" at instant = "+pClock.value().value()+"...");
			while( ! isEmpty() && canDispatch(first = firstEntry().getKey()) ) {
				System.out.println("Message dispatched.");
				FifoOrderMulticast m = remove(first) ;
				m.payload.deliverTo( m.src, Middleware.this) ;
			}
		}
		boolean canDispatch(double messageTime) {
			return messageTime < pClock.value().value();
		}
	}
	
	
}

