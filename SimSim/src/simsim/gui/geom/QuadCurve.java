package simsim.gui.geom;

import java.awt.geom.*;

import simsim.utils.*;

public class QuadCurve extends QuadCurve2D.Double {

	public QuadCurve( XY a, XY b, XY c) {
		super( a.x, a.y, b.x, b.y, c.x, c.y ) ;
	}
	
	public QuadCurve( double x1, double y1, double x2, double y2, double x3, double y3) {
		super( x1, y1, x2, y2, x3, y3) ;
	}
	
	public XY interpolate( double t ) {
		if( t < 0 ) return new XY( super.x1, super.y1) ;
		else if ( t > 1 ) return new XY( super.x2, super.y2 ) ;

		final double ct = 1 - t ;
		final double f0 = ct * ct ;
		final double f1 = 2 * t * ct;
		final double f2 = t * t ;
		return new XY( f2 * x1 + f1 * super.ctrlx + f0 * x2, f2 * y1 + f1 * super.ctrly + f0 * y2 ) ;
	}
}
