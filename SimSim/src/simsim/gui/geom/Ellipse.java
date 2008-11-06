package simsim.gui.geom;

import java.awt.geom.*;

import simsim.utils.XY;

public class Ellipse extends Ellipse2D.Double {

	public Ellipse( XY center, XY radius ) {
		this( center.x, center.y, radius.x, radius.y) ;
	}

	public Ellipse( double cx, double cy, double w, double h ) {
		super( cx - w * 0.5, cy - h * 0.5, w, h) ;
	}
}
