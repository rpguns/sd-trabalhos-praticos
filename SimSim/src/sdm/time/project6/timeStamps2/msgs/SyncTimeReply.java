package sdm.time.project6.timeStamps2.msgs;

import java.awt.*;

import simsim.core.*;

@SuppressWarnings("serial")
public class SyncTimeReply extends Message {
	public double timeStamp ;
	public double referenceTime ;
	
	public SyncTimeReply( double ts, double rt ) {
		super( true, Color.red ) ;
		this.timeStamp = ts ;
		this.referenceTime = rt ;
	}
				
	/* (non-Javadoc)
	 * <b>IMPORTANT!!!</b> Include a copy of this method in every class that extends Message
	 * @see sim.core.Message#deliverTo(sim.net.EndPoint, sim.core.MessageHandler)
	 */
	public void deliverTo( EndPoint src, MessageHandler handler ) {
		((NodeMessageHandler)handler).onReceive( src, this ) ;
	}
}
