package sdm.time.project6.election.msgs;

import simsim.core.*;
import java.awt.*;

@SuppressWarnings("serial")
public class Election extends Message {

	protected int ID;
	
	public Election(int ID ) {
		super( false, Color.RED) ;
		this.ID = ID;
	}
	
	public int getID() {
		return ID;
	}
	
	/* (non-Javadoc)
	 * <b>IMPORTANT!!!</b> Include a copy of this method in every class that extends Message
	 * @see sim.core.Message#deliverTo(sim.net.EndPoint, sim.core.MessageHandler)
	 */
	public void deliverTo( EndPoint src, MessageHandler handler ) {
		((ProcessMessageHandler)handler).onReceive( src, this ) ;
	}	
}