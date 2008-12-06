package sdm.time.project6.vector;

import java.util.* ;

import simsim.core.*;
import sdm.time.project6.vector.msgs.*;
import sdm.time.project6.vector.clocks.*;

public class Middleware extends Node implements MiddlewareMessageHandler {
	
	int myFifo_SeqN = 0 ;
	//Map<EndPoint, FifoQueue> fifoQueues = new HashMap<EndPoint, FifoQueue>() ;
	FifoQueue queueQueue = new FifoQueue() ;
	FifoQueue2 fifo = new FifoQueue2() ;
	VectorClock vClock = new VectorClock(this.index,16);
	TimeStamp last = vClock.value();
	
	
	protected void R_multicast( final Message m ) {
		endpoint.broadcast( m ) ;		
	}
	
	protected void FO_multicast( final Message m ) {
		//endpoint.broadcast( new FifoOrderMulticast( myFifo_SeqN++, endpoint, m ) ) ;
		//endpoint.broadcast( new FifoOrderMulticast( vClock.increment().value(), endpoint, m ) ) ;
	}
	
	protected void TO_multicast( final Message m ) {
		vClock.increment();
		EndPoint sequencer = NodeDB.k2n.get(0).endpoint ;
		endpoint.udpSend(sequencer, new TotalOrderMulticast( endpoint,vClock.value(), m ) ) ; 
	}
	
	public void onReceive(EndPoint src, TotalOrderMulticast m) {
		//FO_multicast( m.payload ) ;
		System.out.println("got one!");
		FifoQueue srcQueue = fifoQueue( src ) ;
		if (src != endpoint) vClock.increment();
		vClock.update(m.ts);
		srcQueue.put( vClock.value(), m ) ;
		srcQueue.dispatch() ;
	}
	
	public void onReceive(EndPoint src, FifoOrderMulticast m) {
			//FifoQueue srcQueue = fifoQueue( src ) ;
			//vClock.increment().update(m.seq);
			//srcQueue.put( vClock.value(), m ) ;
			//srcQueue.dispatch() ;
			if (index == 1)
				System.out.println("got "+m.seq);
			fifo.put(vClock.increment().update(m.seq).value(), m);
			fifo.dispatch();
			
	}
	
	
	private FifoQueue fifoQueue( EndPoint src ) {
		return queueQueue ;
	}
	
	@SuppressWarnings("serial")
	class FifoQueue extends TreeMap<TimeStamp, TotalOrderMulticast> {
		void dispatch() {
				
			while( ! isEmpty()) {
					TimeStamp key = firstEntry().getKey();
					TotalOrderMulticast m = remove(firstEntry().getKey()) ;
				//System.out.println(key+" on process "+index+" from process "+m.src.toString());
				//m.payload.deliverTo( m.src, Middleware.this);
				FifoOrderMulticast message = new FifoOrderMulticast(key,endpoint,m.payload);
				endpoint.broadcast( message ) ;
				
			}
		}
		
		void dispatch2() {
			
			while( ! isEmpty()) {
					TimeStamp key = firstEntry().getKey();
					TotalOrderMulticast m = remove(firstEntry().getKey()) ;
				//System.out.println(key+" on process "+index+" from process "+m.src.toString());
				m.payload.deliverTo( m.src, Middleware.this);
			}
		}
	}
	
	@SuppressWarnings("serial")
	class FifoQueue2 extends TreeMap<TimeStamp, FifoOrderMulticast> {

		void dispatch() {
			
			while( ! isEmpty()) {
					TimeStamp key = firstEntry().getKey();
					FifoOrderMulticast m = remove(firstEntry().getKey()) ;
				//System.out.println(key+" on process "+index+" from process "+m.src.toString());
				m.payload.deliverTo( m.src, Middleware.this);
			}
		}
	}
}

