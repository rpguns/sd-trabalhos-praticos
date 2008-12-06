package sdm.time.project6.vector.msgs;

import simsim.core.*; 
import sdm.time.project6.vector.clocks.*;

@SuppressWarnings("serial")
public class TotalOrderMulticast extends Message {

	public EndPoint src ;
	public Message payload ;
	public TimeStamp ts;
	
	public TotalOrderMulticast( EndPoint src,TimeStamp ts, Message p ) {
		super( true, p.color ) ;
		this.src = src;
		this.payload = p ;
		this.ts = ts;
	}
	
	/* (non-Javadoc)
	 * <b>IMPORTANT!!!</b> Include a copy of this method in every class that extends Message
	 * @see sim.core.Message#deliverTo(sim.net.EndPoint, sim.core.MessageHandler)
	 */
	public void deliverTo( EndPoint src, MessageHandler handler ) {
		((MiddlewareMessageHandler)handler).onReceive( src, this ) ;
	}
}
