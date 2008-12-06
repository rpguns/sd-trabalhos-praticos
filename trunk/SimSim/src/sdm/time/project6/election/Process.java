package sdm.time.project6.election;

import java.awt.*;
import java.awt.geom.*;

import simsim.core.*;
import simsim.utils.*;
import simsim.gui.geom.*;
import sdm.time.project6.election.msgs.*;
import static simsim.core.Simulation.*;


/**
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 *
 */
public class Process extends Middleware implements ProcessMessageHandler {
	
	Area state ;
	Color stateColor, bkgColor ;

	Task docTask ;
	
	public Process() {
		ProcessDB.store(this) ;
		
		bkgColor = Color.white ;
		stateColor = Color.gray ;
		state = new Area( new Circle( 500, 500, 500 ) ) ;
	}

	/**
	 * Initializes this process.
	 * 
	 * Must not call super.init(), because it has already been called by Main to begin this node's clock synchronization.
	 */
	public void exec() {
		new StateDisplay() ;

		docTask = new Task( 20*rg.nextDouble() )  {
			public void run() {
				submitAreaOperation() ;
				this.reSchedule( 60 + 80 * rg.nextDouble() ) ;
			}
		};
	}

	/**
	 * Sends a message to update the state of the group of processes,
	 * Depending on the semantics of the multicast operation used, the state of the processes may diverge.
	 */
	private void submitAreaOperation() {
		char op = OpGenerator.generate() ;
		Shape shape = ShapeGenerator.generate() ;
		TO_multicast( new ShapeOperation( op, shape) ) ;		
		
	}
	
	public void onReceive(EndPoint src, ChangeColor m) {
		stateColor = m.color ;
	}
	
	public void onReceive(EndPoint src, ShapeOperation m) {
		Area area = new Area( m.shape ) ;
		
		switch( m.op ) {
		case '+' :
			state.add( area ) ;
			break ;
		case '-' :
			state.subtract( area) ;	
			break ;			
		case '*' :
			state.intersect( area ) ;
			break ;
		}
		stateColor = m.color ;

		/*
		 * Randomly generate a new operation in response to an incoming operation.
		 */
		if( rg.nextDouble() < 0.04 ) {
			this.submitAreaOperation() ;
		}	
		
		/*
		 * If the state becomes an empty area, update the background color.
		 */
		if( ! state.isEmpty() ) {		
			Color newColor = Color.getHSBColor( rg.nextFloat(), 0.3f + 0.3f * rg.nextFloat(), 0.9f) ;	
			TO_multicast( new ChangeColor( newColor ) ) ;
		}		
	}
	
	
	/**
	 * This class is used to display the state of this process in its own window.
	 * @author Sérgio Duarte
	 *
	 */
	class StateDisplay implements Displayable {

		StateDisplay() {
			Gui.addDisplayable( this.toString(), this, 5.0) ;
			
			int s = (int) Math.sqrt( ProcessDB.size() ) ;			
			int i = index / s, j = index % s ;
			XY size = new XY(650/s, 650/s) ;
			XY corner = new XY(360, 0 ).add( new XY( (size.x + 4)* i, (size.y + 24) * j )) ;
			Gui.setFrameRectangle( this.toString(), corner.X(), corner.Y(), size.X(), size.Y()) ;
			Gui.setFrameTransform( this.toString(), 1000, 1000, 0, true) ;
		}
		
		public void display(Graphics2D gu, Graphics2D gs) {
			gs.setBackground(bkgColor) ;
			gs.clearRect(0, 0, 1000, 1000) ;
			
			gs.setStroke( new BasicStroke(30.0f)) ;
			gs.setColor( stateColor.darker() ) ;
			gs.draw( state ) ;
			gs.setColor( stateColor ) ;
			gs.fill( state ) ;
		}
		
		public String toString() {
			return "Process:" + index ;
		}
	}
}


class ShapeGenerator {	
	static Shape generate() {
		XY center = new XY( rg.nextDouble() * 1000, rg.nextDouble() * 1000 ) ;
		if( rg.nextBoolean() ) {
			return new Circle( center, 200 + rg.nextDouble() * 300) ;
		} else {
			return new simsim.gui.geom.Rectangle( center.x, center.y, 200 + rg.nextDouble() * 200, 200 + rg.nextDouble() * 200 ) ;			
		}
	}
}

class OpGenerator {
	static char generate() {
		int v = rg.nextInt(100) ;
		if( v < 5 ) return '-' ;
		else if( v < 10 ) return '*' ;
		else return '+' ;
	}
}