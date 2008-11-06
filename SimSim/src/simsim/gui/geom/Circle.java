package simsim.gui.geom;

import java.awt.geom.*;

import simsim.utils.*;

public class Circle extends Ellipse2D.Double {

	public Circle( double cx, double cy, double radius ) {
		super( cx - radius * 0.5, cy - radius * 0.5, radius, radius) ;
	}

	public Circle( XY center, double radius ) {
		this( center.x, center.y, radius ) ;
	}

}
