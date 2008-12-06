package sdm.time.project6.election.msgs;

import java.awt.* ;

import simsim.core.*;

@SuppressWarnings("serial")
public class ChangeColor extends Message {

	
	public ChangeColor( Color color) {
		super( true, color ) ;
	}
	
	/* (non-Javadoc)
	 * <b>IMPORTANT!!!</b> Include a copy of this method in every class that extends Message
	 * @see sim.core.Message#deliverTo(sim.net.EndPoint, sim.core.MessageHandler)
	 */
	public void deliverTo( EndPoint src, MessageHandler handler ) {
		((ProcessMessageHandler)handler).onReceive( src, this ) ;
	}	
}