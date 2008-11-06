package sdm.epidemic.examples.C.msgs;

import java.awt.* ;

import simsim.core.*;

public class InfectNode extends Message {
		
	public InfectNode() {
		super( true, Color.red ) ;
	}
	
	/* (non-Javadoc)
	 * <b>IMPORTANT!!!</b> Include a copy of this method in every class that extends Message
	 * @see sim.core.Message#deliverTo(sim.net.EndPoint, sim.core.MessageHandler)
	 */
	public void deliverTo( EndPoint src, MessageHandler handler ) {
		((ExtendedMessageHandler)handler).onReceive( src, this ) ;
	}
}
