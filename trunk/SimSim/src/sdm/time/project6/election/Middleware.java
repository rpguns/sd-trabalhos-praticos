package sdm.time.project6.election;

import java.util.* ;

import simsim.core.*;
import sdm.time.project6.election.msgs.*;

public class Middleware extends Node implements MiddlewareMessageHandler {

	EndPoint sequencer = null;
	int myID = new Random().nextInt();
	int myFifo_SeqN = 0 ;
	Map<EndPoint, FifoQueue> fifoQueues = new HashMap<EndPoint, FifoQueue>() ;
	Task electionTask = null;
	boolean startedElection = false;


	protected void R_multicast( final Message m ) {
		endpoint.broadcast( m ) ;		
	}

	protected void FO_multicast( final Message m ) {
		endpoint.broadcast( new FifoOrderMulticast( myFifo_SeqN++, endpoint, m ) ) ;
	}

	protected void TO_multicast( final Message m ) {
		if(sequencer == null) {
			System.out.println("Undergoing election...");
			beginElection();
		}
		else {
			endpoint.udpSend(sequencer, new TotalOrderMulticast( endpoint, m ) ) ; 
			System.out.println("Current sequencer is "+((Node)sequencer.handler).index);
		}
	}

	public void onReceive(EndPoint src, TotalOrderMulticast m) {
		FO_multicast( m.payload ) ;
	}

	public void onReceive(EndPoint src, FifoOrderMulticast m) {
		FifoQueue srcQueue = fifoQueue( src ) ;
		srcQueue.put( m.seq, m ) ;
		srcQueue.dispatch() ;
	}

	public void beginElection() {
		endpoint.broadcast(new Election(myID));
		electionTask = new Task(10) {
			public void run() {
				endElection();
			}
		};
	}
	
	public void endElection() {
		if (sequencer == null) {
			sequencer = endpoint;
			endpoint.broadcast(new Coordinator());
			startedElection = false;
		}
		
	}

	public void onReceive(EndPoint src, Election m) {
		if (myID > m.getID() && !startedElection) {
			System.out.println(this.index);
			beginElection();
			udpSend(src,new ElectionAck());
		}
	}

	public void onReceive(EndPoint src, ElectionAck m) {
		if (electionTask != null)
			electionTask.cancel();
	}
	
	public void onReceive(EndPoint src, Coordinator m) {
		sequencer = src;
		startedElection = false;
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

