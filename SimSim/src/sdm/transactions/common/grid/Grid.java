package sdm.transactions.common.grid;

import java.io.* ;
import java.awt.* ;
import java.awt.geom.*;

import simsim.core.*;

@SuppressWarnings("serial")
public class Grid implements GridOperations, Displayable, Serializable {

	private static final Color colors[] = { Color.green, Color.red, Color.blue, Color.magenta, Color.orange, Color.pink, Color.darkGray } ;
	private static final RectangularShape shapes[] = { new Ellipse2D.Double(0,0,0,0),  new RoundRectangle2D.Double(0,0,0,0, 2, 2 )} ;

	int color[][] ;
	int shape[][] ;
	int rows, cols ;
	
	public Grid( int rows, int cols ) {
		this.rows = rows ;
		this.cols = cols ;
		this.color = new int[rows][cols] ;
		this.shape = new int[rows][cols] ;
		
		for( int i = 0 ; i < rows ; i++ )
			for( int j = 0 ; j < cols ; j++ ) {
				color[i][j] = 0 ;
				shape[i][j] = 1 ;
			}
	}

	private Grid( Grid other ) {
		this.rows = other.rows ;
		this.cols = other.cols ;
		this.color = new int[rows][cols] ;
		this.shape = new int[rows][cols] ;
		for( int i = 0 ; i < rows ; i++ )
			for( int j = 0 ; j < cols ; j++ ) {
				color[i][j] = other.color[i][j] ;
				shape[i][j] = other.shape[i][j] ;
			}	
	}
	
	public int rows() {
		return rows; 
	}
	
	public int cols() {
		return cols;
	}
	
	public Grid makeCopy() {
		return new Grid( this ) ;
	}
	
	//----------------------------------------------------------------------------------------------	
	// Implementation of Interface GridOperations
	public int[] gridSize( long tid ) {
		return new int[] { rows, cols } ;
	}	

	
	public int readColor( long tid, int i, int j) {		
		return color[i][j] ;		
	}
 

	public int readShape( long tid, int i, int j) {
		return shape[i][j] ;
	}


	public void writeColor( long tid, int i, int j, int v) {
		v = v % colors.length ;
		color[i][j] = v ;
	}


	public void writeShape( long tid, int i, int j, int v) {
		v = v % shapes.length ;		
		shape[i][j] = v ;
	}
		
//----------------------------------------------------------------------------------------------	
// Visualization

	//	public double width( int nServers ) {
//		int s = (int) Math.ceil( Math.sqrt( nServers ) ) ;
//		return 1000.0 / (s * rows);
//	}
//	
//	public double height( int nServers ) {
//		int s = (int) Math.ceil( Math.sqrt( nServers ) ) ;
//		return 1000.0 / (s * cols);
//	}
	
	public void display( int i, int j, Graphics2D gs, int w, boolean offline ) {
		final double sX = 1000.0 / rows, sY = 1000.0 / cols ; 
		
		if( offline )
			gs.setColor( Color.lightGray ) ;
		else
			gs.setColor( colors[ color[i][j] ] ) ;
		
		
		double x = i * sX, y = j * sY ;
		RectangularShape s = shapes[ shape[i][j] ] ;
		s.setFrame( x+2, y+2, sX-4, sY-4 ) ;
		gs.fill(s) ;

		if( w > 0 ) {
			gs.setColor( Color.black ) ;
			gs.drawString("" + w, (float)(x + sX/2 - 5), (float)(y + sY/2 + 5) );
		}
	}
	
	public void display( Graphics2D gu, Graphics2D gs) {
		for( int i = 0 ; i < rows ; i++ ) {			
			for( int j = 0 ; j < cols ; j++ ) 
				display( i, j, gs, 0, false ) ;
		}
	}
}