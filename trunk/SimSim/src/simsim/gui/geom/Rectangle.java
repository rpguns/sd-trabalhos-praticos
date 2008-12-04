package simsim.gui.geom;

import simsim.utils.*;

import java.awt.geom.* ;

public class Rectangle extends Rectangle2D.Double {

	public Rectangle( XY p, double w, double h) {
		super( p.x - w * 0.5, p.y - h * 0.5, w, h ) ;
	}
	
	public Rectangle( double x, double y, double w, double h) {
		super( x - w * 0.5, y - h * 0.5, w, h ) ;
	}
}
