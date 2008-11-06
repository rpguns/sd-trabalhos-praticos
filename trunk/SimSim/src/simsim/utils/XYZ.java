package simsim.utils;

import static simsim.core.Simulation.rg;


/**
 * A class used to represent a point in 2D Cartesian space.
 * 
 * @author smd
 *
 */
public class XYZ {
	public double x, y, z ;

	public XYZ() {
		x = rg.nextDouble() ; y = rg.nextDouble() ; z = rg.nextDouble() ;
	}
	
	public XYZ( double x, double y, double z ) {
		this.x = x ; this.y = y ; this.z = z ;
	}
	
	public XYZ scale( double s ) {
		x *=s ; y *=s ; z *=s;
		return this ;
	}
	
	public double distance( XYZ other ) {
		double dx = x - other.x ; double dy = y - other.y ; double dz = z - other.z ;
		return Math.sqrt( dx * dx + dy * dy + dz * dz );
	}
	
	public int X() {
		return (int)x ;
	}

	public int Y() {
		return (int)y ;	
	}

	public int Z() {
		return (int)z ;	
	}

	public String toString() {
		return String.format("(%1.3f, %1.3f, %1.3f)", x, y, z ) ;
	}
}