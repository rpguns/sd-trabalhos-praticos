package simsim.core;

import simsim.utils.*;

import java.awt.*;
import java.awt.geom.* ;

import static simsim.core.Scheduler.*;

/**
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 *
 */
public class SoftwareClock implements Displayable {

	XY xy ;
	double D = 0 ;
	double T0, H0 ; 
	double drift, skew ;
	final double W = 100 ;
	
	private ClockAdjustmentTask cat ;
	private boolean isMasterClock ;
	
	
	SoftwareClock( AbstractNode owner, XY p ) {
		xy = p ;
		H0 = H() - 1e-20 ;
		isMasterClock = false ;
		skew = 0.01 * Simulation.rg.nextDouble() ;
		drift = 1 + 5e-5 * (1.0 - 2 * Simulation.rg.nextDouble()) ;
		cat = new ClockAdjustmentTask(owner, 1e80, 1) ;
	}

	
	/**
	 * Tells if this is a reference clock, on which adjustments do not have an effect.
	 * @return an indication if this is a reference clock.
	 */
	public boolean isMasterClock() {
		return isMasterClock ;
	}
	
	/**
	 * Sets if this will be a reference clock or not.
	 * @param val - flag that indicates if the clock is a reference clock or not.
	 */
	public void isMasterClock( boolean val ) {
		if( val ) {
			D = 0 ;
			cat.cancel() ;
			isMasterClock = val ;
			drift = 1 ; skew = 0 ;
		}
	}
	
	/**
	 * @return The time as measured by this clock, already taking into account any adjustments made.
	 */
	public double currentTime() {
		T0 = Math.max(T0 + 1e-9, T()) ;  //Ensures time advances at least 1 nanosecond every time the clock is read
		return T0 ;
	}
	
	/**
	 * @return The time as measured by the pseudo-hardware clock.
	 */
	private double H() {
		return Scheduler.now();
	}
	
	/**
	 *  Implements a different clock for each node, by using different initial values for drift and skew.
	 * @return The time as measured by this clock, already taking into account any adjustments made.
	 */
	private double T() {
		return drift * H() + skew ;
	}

	/**
	 * Instructs the runtime system to adjust the software clock given an estimate of the offset of this clock relative to a reference clock
	 * @param d - the estimate of the offset of this clock, relative to a reference clock.
	 */
	public void adjustClock( double d ) {
		if( ! isMasterClock ) {
			D = d ;
			H0 = H() + W ;
			cat.run() ;
		}
	}
	
	/**
	 * This periodic tasks attempts to match the current clock to the reference clock, by  adjusting the clocks drift and skew once per second. 
	 * @author smd
	 *
	 */

	class ClockAdjustmentTask extends PeriodicTask {
		
		ClockAdjustmentTask( AbstractNode owner, double d, double p ) {
			super(owner, d, p) ;
		}
		
		/**
		 * When the clock is adjusted and a offset is supplied this task will attempt to correct the clock to 
		 * match the drift and skew of the reference clock.
		 * 
		 * A new drift is computed assuming the adjustment will succeed in W/2 to W seconds.
		 * @author smd
		 *
		 */
		public void run() {
			if( isMasterClock ) return ;
			
			T0 = T() ; //To ensures time is a monotonic function...
			double newDrift = 1 + D / Math.max( W/2, H0 - H() ) ; 
			skew += (drift - newDrift ) * H() ;
			drift = newDrift ;
			
			cat.reSchedule(1) ;
		}
	}

	public void display(Graphics2D gu, Graphics2D gs) {
		double t0 = H() ;
		double t = drift * t0 + skew ;
		final double SCALE = 50000 ;
		
		XY a = new XY( xy.x-10, xy.y + SCALE * D) ;
		XY b = new XY( xy.x +0, xy.y + SCALE * (t - t0 )) ;
		XY c = new XY( xy.x+10, xy.y + 100*SCALE * (drift - 1)) ;
		
		gs.setColor( Color.orange) ;
		gs.setStroke( new BasicStroke(5.0f)) ;
		gs.draw( new Line2D.Double( a.x, xy.y, a.x, a.y ) ) ;
		gs.setColor( Color.blue ) ;
		gs.setStroke( new BasicStroke(5.0f)) ;
		gs.draw( new Line2D.Double( b.x, xy.y, b.x, b.y ) ) ;		

		
		gs.setColor( Color.magenta ) ;
		gs.setStroke( new BasicStroke(5.0f)) ;
		gs.draw( new Line2D.Double( c.x, xy.y, c.x, c.y ) ) ;			
	}
}

