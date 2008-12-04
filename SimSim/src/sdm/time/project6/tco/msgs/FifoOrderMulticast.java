package sdm.time.project6.tco.msgs;

import simsim.core.*; 

@SuppressWarnings("serial")
public class FifoOrderMulticast extends Message {

	public int seq ;
	public EndPoint src ;
	public Message payload ;
	
	public FifoOrderMulticast( int seq, EndPoint src, Message p ) {
		super( true, p.color ) ;
		this.seq = seq ;
		this.payload = p ;
	}
	
	/* (non-Javadoc)
	 * <b>IMPORTANT!!!</b> Include a copy of this method in every class that extends Message
	 * @see sim.core.Message#deliverTo(sim.net.EndPoint, sim.core.MessageHandler)
	 */
	public void deliverTo( EndPoint src, MessageHandler handler ) {
		((MiddlewareMessageHandler)handler).onReceive( src, this ) ;
	}
}
