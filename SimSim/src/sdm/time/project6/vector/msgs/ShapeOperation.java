package sdm.time.project6.vector.msgs;

import java.awt.* ;

import simsim.core.*;

@SuppressWarnings("serial")
public class ShapeOperation extends Message {

	public char op ;
	public Shape shape ;
	
	public ShapeOperation( char op, Shape shape) {
		super( true, Color.yellow ) ;
		this.op = op ;
		this.shape = shape ;
	}
	
	/* (non-Javadoc)
	 * <b>IMPORTANT!!!</b> Include a copy of this method in every class that extends Message
	 * @see sim.core.Message#deliverTo(sim.net.EndPoint, sim.core.MessageHandler)
	 */
	public void deliverTo( EndPoint src, MessageHandler handler ) {
		((ProcessMessageHandler)handler).onReceive( src, this ) ;
	}	
}