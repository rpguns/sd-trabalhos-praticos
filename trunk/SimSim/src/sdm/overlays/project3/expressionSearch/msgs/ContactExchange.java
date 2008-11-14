package sdm.overlays.project3.expressionSearch.msgs;

import java.awt.* ;
import java.util.* ;

import simsim.core.*;

/**
 * Example of a custom message sub-type.
 * 
 * Messages sub-types may include fields and methods. Static fields are not allowed, because they violate
 * the assumptions of a distributed system. Moreover, you should treat messages as read-only after they 
 * are created, sent or received. If you need to modify messages, as they as forwarded across the system,
 * then extend SerializedMessage instead of Message. This ensures that a private copy is delivered to
 * each recipient. This form is much slower and should be avoided if possible. 
 * You can mix Message and SerializedMessages.
 * 
 * @author smd
 *
 */
public class ContactExchange extends Message {
	
	public ArrayList<EndPoint> seeds ;

	public ContactExchange( Collection<EndPoint> x ) {
		super( false, Color.black ) ;
		seeds = new ArrayList<EndPoint>( x ) ;
	}

	
	/* (non-Javadoc)
	 * <b>IMPORTANT!!!</b> Include a copy of this method in every class that extends Message
	 * @see sim.core.Message#deliverTo(sim.net.EndPoint, sim.core.MessageHandler)
	 */
	public void deliverTo( EndPoint src, MessageHandler handler ) {
		((ExtendedMessageHandler)handler).onReceive( src, this ) ;
	}
}
