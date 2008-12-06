package sdm.time.project6.election.msgs;

import simsim.core.*; 

@SuppressWarnings("serial")
public class TotalOrderMulticast extends Message {

	public EndPoint src ;
	public Message payload ;
	
	public TotalOrderMulticast( EndPoint src, Message p ) {
		super( true, p.color ) ;
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
