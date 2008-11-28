package sdm.time.physicalCristianOffset2.msg;

import java.awt.* ;

import simsim.core.*;

public class OffsetMessage extends Message {
	public double offset;
	
	public OffsetMessage( double offset) {
		this.offset = offset;
	}
	
	/* (non-Javadoc)
	 * <b>IMPORTANT!!!</b> Include a copy of this method in every class that extends Message
	 * @see sim.core.Message#deliverTo(sim.net.EndPoint, sim.core.MessageHandler)
	 */
	public void deliverTo( EndPoint src, MessageHandler handler ) {
		((ExtendedMessageHandler)handler).onReceive( src, this ) ;
	}
	
}
