package simsim.gui.geom;

import java.awt.geom.* ;

import simsim.utils.XY;

public class Line extends Line2D.Double{

	public Line( XY a, XY b ) {
		super( a.x, a.y, b.x, b.y) ;
	}
	
	public Line( double x1, double y1, double x2, double y2) {
		super( x1, y1, x2, y2) ;
	}
	
	public XY interpolate( double t ) {
		t = Math.max(0, Math.min( 1, t));
		return new XY( t * x1 + (1-t) * x2, t * y1 + (1-t) * y2 ) ;
	}
}
