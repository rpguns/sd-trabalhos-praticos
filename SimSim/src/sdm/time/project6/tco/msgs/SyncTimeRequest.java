package sdm.time.project6.tco.msgs;

import java.awt.* ;

import simsim.core.*;

@SuppressWarnings("serial")
public class SyncTimeRequest extends Message {
	public double timeStamp ;
	
	public SyncTimeRequest( double ts) {
		super( true, Color.green ) ;
		this.timeStamp = ts;
	}
	
	/* (non-Javadoc)
	 * <b>IMPORTANT!!!</b> Include a copy of this method in every class that extends Message
	 * @see sim.core.Message#deliverTo(sim.net.EndPoint, sim.core.MessageHandler)
	 */
	public void deliverTo( EndPoint src, MessageHandler handler ) {
		((NodeMessageHandler)handler).onReceive( src, this ) ;
	}
	
}
