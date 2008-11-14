package sdm.overlays.project4.dynamic.msgs;

import java.awt.* ;

import sdm.overlays.project4.dynamic.*;
import simsim.core.*;
import simsim.utils.*;
import simsim.gui.geom.*;
import static simsim.core.Simulation.* ;


public class NotifyMessage extends Message {
	
	protected EndPoint pred;
	protected double key;
	
	public NotifyMessage( double key, EndPoint pred ) {
		super(true, Color.getHSBColor( rg.nextFloat(), 0.6f, 0.6f) );
		this.pred = pred;
	}
	
	public EndPoint getPredecessor() {
		return pred;
	}
	
	public double getPredKey() {
		return key;
	}
	
	/* (non-Javadoc)
	 * <b>IMPORTANT!!!</b> Include a copy of this method in every class that extends Message
	 * @see sim.core.Message#deliverTo(sim.net.EndPoint, sim.core.MessageHandler)
	 */
	public void deliverTo( EndPoint src, MessageHandler handler ) {
		((ExtendedMessageHandler)handler).onReceive( src, this ) ;
	}
	
	public void display(  Graphics2D gu,  Graphics2D gs, EndPoint src, EndPoint dst, double t, double p) {
		
		Node a = (Node) src.handler ;
		Node b = (Node) dst.handler ;

		XY m = a.pos.add( b.pos).mult( 0.5) ;    	
		XY c = new XY( m.x + (500-m.x) / 1, m.y + (500 - m.y) / 1) ;

    	gs.setColor( color ) ;
    	
    	QuadCurve qc = new QuadCurve( a.pos, c, b.pos) ;
    	gs.setStroke( new BasicStroke( t < 0 ? 0.5f : 3.0f)) ;  
    	gs.draw( qc ) ;

    	if( t >= 0)
      		gs.fill( new Circle( qc.interpolate( p), 12) ) ;
  	
	}
}
