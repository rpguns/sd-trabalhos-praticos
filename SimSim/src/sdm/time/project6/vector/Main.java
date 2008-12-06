package sdm.time.project6.vector;

import java.awt.*;
import java.util.*;

import simsim.core.*;
import simsim.ext.charts.*;

public class Main extends Simulation implements Displayable {
	
	public static final int TOTAL_PROCESSES = 16 ;

	Main() {
		super( 20, EnumSet.of( DisplayFlags.SIMULATION, DisplayFlags.TIME, DisplayFlags.SPANNER, DisplayFlags.TRAFFIC) ) ;
	}

	Main init() {
		Spanner.setThreshold( 1.51) ;

		final XYLineChart chart = new XYLineChart("Maximum clock skew", 5.0, "Maximum Skew (ms)", "time(s)") ;
		chart.setSeriesLinesAndShapes("s0", true, false) ;

		Gui.setFrameRectangle("MainFrame", 0, 0, 350, 350) ;
		Gui.setFrameRectangle("Maximum clock skew", 0, 375, 350, 350) ;
		Gui.setFrameTransform("MainFrame", 1000, 1000, 0.2, true) ; 
				
		//Create the simulation nodes
		for( int i = 0 ; i < TOTAL_PROCESSES ; i++ ) 
			new Process() ;

		Node masterNode = NodeDB.randomNode() ;
		
		//Initialize the simulation nodes
		for( Node i : NodeDB.nodes ) 
			i.init( masterNode.address ) ;
		
		new PeriodicTask( 30, 5) {
			public void run() {
				int N = NodeDB.size() ;
				double D = Double.MIN_VALUE ; 
				for( int i = 0 ; i < N ; i++ ) {
					Node nI = NodeDB.nodes.get(i) ;
					for( int j = 0 ; j < i ; j++ ) {
						double skew = nI.currentTime() - NodeDB.nodes.get(j).currentTime() ;
						D = Math.max( D, Math.abs( skew )) ;
					}
				}
				chart.getSeries("s0").add( currentTime(), 1000*D ) ;
			}
		};
		
		System.out.println("Node init complete...") ;
		
		
		new Task(1000) {
			public void run() {
				//Initialize the simulation nodes
				for( Process i : ProcessDB.processes ) 
					i.exec();
				
				System.out.println("Process exec complete...") ;
				setSimulationMaxTimeWarp(5) ;
			}
		} ;
		
		
		setSimulationMaxTimeWarp(500) ;
		return this ;
	}
	
	public void display(Graphics2D gu, Graphics2D gs) {
		gs.setStroke( new BasicStroke(0.1f)) ;
		
		for( Node i : NodeDB.nodes )
			i.display(gu, gs) ;		
	}

	public static void main( String[] args ) throws Exception {
		
		Globals.set("Sim_RandomSeed", 9L);
		Globals.set("Net_RandomSeed", 2L);

		Globals.set("Net_Jitter", 2.0);

		
		Globals.set("Net_FontSize", 16.0f ) ;
		Globals.set("Net_Euclidean_NodeRadius", 30.0);
		Globals.set("Net_Euclidean_CostFactor", 0.0001);		
		Globals.set("Net_Euclidean_DisplayNodeLabels", true);
		Globals.set("Net_Euclidean_ToggleDrawNodeMode", true ) ;
		Globals.set("Net_Euclidean_MinimumNodeDistance", 120.0);

		new Main().init().start() ;
	}
}
