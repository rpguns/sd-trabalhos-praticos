package sdm.overlays.project4.wordSearch.msgs;

import java.awt.* ;

import sdm.overlays.project4.wordSearch.*;
import sdm.overlays.words.*;
import simsim.core.*;
import simsim.utils.*;
import simsim.gui.geom.*;
import static simsim.core.Simulation.* ;


public class PutMessage extends Message {
	
	protected Word word;
	protected EndPoint origin;
	protected double dst;
	protected int hopCount = 1;
	
	public PutMessage( Word word, EndPoint origin ) {
		super(false, Color.getHSBColor( rg.nextFloat(), 0.6f, 0.6f) );
		this.word = word;
		this.origin = origin;
		this.dst = word.dHashValue();
	}
	
	public PutMessage( PutMessage other ) {
		this( other.getWord(), other.getOrigin() ) ;
		this.color = other.color ;
		this.hopCount = other.hopCount + 1 ;
	}
	
	public Word getWord() {
		return word;
	}
	
	public EndPoint getOrigin() {
		return origin;
	}
	
	public double getDst() {
		return dst;
	}
	
	/* (non-Javadoc)
	 * <b>IMPORTANT!!!</b> Include a copy of this method in every class that extends Message
	 * @see sim.core.Message#deliverTo(sim.net.EndPoint, sim.core.MessageHandler)
	 */
	public void deliverTo( EndPoint src, MessageHandler handler ) {
		((ExtendedMessageHandler)handler).onReceive( src, this ) ;
	}
	
	public void display(  Graphics2D gu,  Graphics2D gs, EndPoint src, EndPoint dst, double t, double p) {
		/*
		Node a = (Node) src.handler ;
		Node b = (Node) dst.handler ;

		XY m = a.pos.add( b.pos).mult( 0.5) ;    	
		XY c = new XY( m.x + (500-m.x) / hopCount, m.y + (500 - m.y) / hopCount) ;

    	gs.setColor( color ) ;
    	
    	QuadCurve qc = new QuadCurve( a.pos, c, b.pos) ;
    	gs.setStroke( new BasicStroke( t < 0 ? 0.5f : 3.0f)) ;  
    	gs.draw( qc ) ;

    	if( t >= 0)
      		gs.fill( new Circle( qc.interpolate( p), 12) ) ;*/
  	
	}
}
