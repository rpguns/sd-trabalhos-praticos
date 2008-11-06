package simsim.core;

import java.awt.*;
import simsim.gui.geom.*;


/**
 * This class is the base class for creating the messages to be exchanged by the nodes in the simulation.
 * 
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 */

abstract public class Message implements EncodedMessage {
	
	protected boolean visible ;
	
	public Color color = Color.DARK_GRAY ;

	/**
	 * Creates a new Message, which by default is not visible/displayed. 
	 */
	protected Message() {
		this( false, Color.gray ) ;
	}
	
	/**
	 * Creates a new Message.
	 * @param visible Tells if the message should be shown when message traffic is displayed in the simulator.
	 * @param color The color used to show the message.
	 */
	protected Message( boolean visible, Color color ) {
		this.color = color ;
		this.visible = visible ;
	}
	
	/**
	 * This method sets the color used for this message when message traffic is drawn in the simulator.
	 * @param color New color to be used.
	 */
	public void setColor( Color color ) {
		this.color = color ;
	}
	
	/**
	 * This method returns the color used to render this message in the simulator.
	 * @return the color used to render this message in the simulator.
	 */
	public Color getColor() {
		return color ;
	}
	
	/* (non-Javadoc)
	 * The encoding of a Message is the object itself, unlike SerializedMessages whose encoding a sequence of bytes.
	 * So it returns itself. So must be cared when the same object/message is sent to multiple nodes. Changes to the message will be
	 * visible globally.
	 * @see simsim.core.EncodedMessage#decode()
	 */
	public Message decode() {
		return this ;
	}

	/* (non-Javadoc)
	 * The encoding of a Message is the object itself, unlike SerializedMessages whose encoding a sequence of bytes.
	 * So it returns itself. So must be cared when the same object/message is sent to multiple nodes. Changes to the message will be
	 * visible globally.
	 * @see simsim.core.EncodedMessage#decode()
	 */
	public EncodedMessage encode() {
		return this ;
	}

	/* (non-Javadoc)
	 * By default, messages have a length of zero bytes.
	 * 
	 * @see simsim.core.EncodedMessage#length()
	 */
	public int length() {
		return 0 ;
	}
	
    /**
     * Tells if the message is to visible when message traffic is displayed.
     * @return The visibility status of the message.
     */
    public boolean isVisible() {
    	return visible ;
    }

	/**
	 * This method has to be overridden in all message sub-types, derived from this class.
	 * 
	 * @param src - Source of the message.
	 * @param handler - Handler of the message.
	 */
	public void deliverTo( EndPoint src, MessageHandler handler ) {
		throw new RuntimeException("deliverTo() not overridden... in: " + this.getClass() ) ;
	}
	
	/**
	 * This method has to be overridden in all message sub-types, derived from this class.
	 * 
	 * @param ch - Source of the message.
	 * @param handler - Handler of the message.
	 */
	public void deliverTo( TcpChannel ch, MessageHandler handler ) {
		throw new RuntimeException("deliverTo() not overridden... in: " + this.getClass() ) ;
	}
	
	/**
	 * This method is called (automatically) for all visible message when traffic is displayed in the simulator.
	 * A line is drawn from the source endpoint to the destination endpoint. 
	 * A circle is drawn in a position along the line interpolated to reflect the progress of the message according to its latency, based on the current time.
	 */
	public void display( Graphics2D gu, Graphics2D gs, EndPoint src, EndPoint dst,  double t, double p) {
		gs.setColor( color) ;
		
		Line l = new Line( src.address.pos, dst.address.pos ) ;
	    gs.draw( l ) ;
      	gs.fill( new Circle( l.interpolate( p ), 10) ) ;
	}
}
