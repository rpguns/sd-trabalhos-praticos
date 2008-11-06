package sdm.epidemic.examples.A.msgs;

import java.awt.* ;

import simsim.core.*;

/**
 * Example of a custom message sub-type.
 * 
 * Messages sub-types may include fields and methods. Static fields are not allowed, because they violate
 * the assumptions of a distributed system. Moreover, you should treat messages as read-only after they 
 * are created, sent or received. If you need to modify messages, as they as forwarded across the system,
 * then extend SerializedMessage instead of Message. This ensures that a private copy is delivered to
 * each recipient. That form is much slower and should be avoided if possible. 
 *
 * Message and SerializedMessages CAN BE mixed.
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 *
 */
public class InfectNode extends Message {
	
	// Create a new InfectNode message, which will be visible and colored red in the GUI
	// Provided traffic monitoring is enabled. Include DisplayFlags.TRAFFIC in the instantiation
	// of the simulation object
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
