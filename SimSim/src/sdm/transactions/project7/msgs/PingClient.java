package sdm.transactions.project7.msgs;

import java.awt.* ;

import simsim.core.*;
import static simsim.core.Simulation.* ;


public class PingClient extends Message {
	
	final static long serialVersionUID = 0L;
	
	public long tid ;
	
	public PingClient( long tid ) {
		super(true, Color.getHSBColor( rg.nextFloat(), 0.6f, 0.6f) );
		this.tid = tid;
	}

	/* (non-Javadoc)
	 * <b>IMPORTANT!!!</b> Include a copy of this method in every class that extends Message
	 * @see sim.core.Message#deliverTo(sim.net.EndPoint, sim.core.MessageHandler)
	 */
	public void deliverTo( EndPoint src, MessageHandler handler ) {
		((ExtendedMessageHandler)handler).onReceive( src, this ) ;
	}
	
}
	
