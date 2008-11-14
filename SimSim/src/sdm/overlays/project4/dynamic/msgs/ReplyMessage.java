package sdm.overlays.project4.dynamic.msgs;

import java.awt.* ;
import java.util.Map;

import sdm.overlays.project4.dynamic.*;
import sdm.overlays.words.*;
import simsim.core.*;
import simsim.utils.*;
import simsim.gui.geom.*;
import static simsim.core.Simulation.* ;


public class ReplyMessage extends Message {
	
	protected Map<EndPoint,Pair<Word,Word>> matchingResults;
	protected int nNodes;
	protected String pattern1,pattern2;
	protected int hopCount = 1;
	
	public ReplyMessage( String pattern1, String pattern2, Map<EndPoint,Pair<Word,Word>> matchingResults, int nNodes ) {
		super(true, Color.getHSBColor( rg.nextFloat(), 0.6f, 0.6f) );
		this.matchingResults = matchingResults;
		this.pattern1 = pattern1;
		this.pattern2 = pattern2;
		this.nNodes = nNodes;
	}
	
	public String getPattern1() {
		return pattern1;
	}
	
	public String getPattern2() {
		return pattern2;
	}
	
	public Map<EndPoint,Pair<Word,Word>> getMatchingResults() {
		return matchingResults;
	}
	
	public int getNodes() {
		return nNodes;
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
		XY c = new XY( m.x + (500-m.x) / hopCount, m.y + (500 - m.y) / hopCount) ;

    	gs.setColor( color ) ;
    	
    	QuadCurve qc = new QuadCurve( a.pos, c, b.pos) ;
    	gs.setStroke( new BasicStroke( t < 0 ? 0.5f : 3.0f)) ;  
    	gs.draw( qc ) ;

    	if( t >= 0)
      		gs.fill( new Circle( qc.interpolate( p), 12) ) ;
  	
	}
}
