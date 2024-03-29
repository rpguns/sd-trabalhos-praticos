package sdm.time.project6.election.msgs;

import simsim.core.*;
import java.awt.*;

@SuppressWarnings("serial")
public class Coordinator extends Message {

	
	public Coordinator() {
		super( false, Color.RED) ;
	}
	
	/* (non-Javadoc)
	 * <b>IMPORTANT!!!</b> Include a copy of this method in every class that extends Message
	 * @see sim.core.Message#deliverTo(sim.net.EndPoint, sim.core.MessageHandler)
	 */
	public void deliverTo( EndPoint src, MessageHandler handler ) {
		((MiddlewareMessageHandler)handler).onReceive( src, this ) ;
	}	
}