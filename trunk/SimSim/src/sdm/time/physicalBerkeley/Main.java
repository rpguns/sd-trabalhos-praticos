package sdm.time.physicalBerkeley;

import java.awt.*;
import java.util.*;

import simsim.core.*;
import simsim.ext.charts.*;

public class Main extends Simulation implements Displayable {
	
	public static final int TOTAL_NODES = 32 ;

	Main() {
		super( 20, EnumSet.of( DisplayFlags.SIMULATION, DisplayFlags.TIME, DisplayFlags.SPANNER, DisplayFlags.TRAFFIC) ) ;
	}

	Main init() {
		Spanner.setThreshold( 2.0) ;

		final XYLineChart chart = new XYLineChart("Maximum clock skew", 5.0, "Maximum Skew (ms)", "time(s)") ;
//		chart.setYRange( false, 0, 100 ) ;
		chart.setSeriesLinesAndShapes("s0", true, false) ;

		Gui.setFrameRectangle("MainFrame", 0, 0, 480, 480) ;
		Gui.setFrameRectangle("Maximum clock skew", 484, 0, 480, 480) ;
		//Create the simulation nodes
		for( int i = 0 ; i < TOTAL_NODES ; i++ ) 
			new Node() ;

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
		
		System.out.println("Init complete...") ;
		super.setSimulationMaxTimeWarp(50) ;
		return this ;
	}
	

	public static void main( String[] args ) throws Exception {
		
		Globals.set("Sim_RandomSeed", 9L);
		Globals.set("Net_RandomSeed", 1L);

		Globals.set("Net_Jitter", 0.5);

		Globals.set("Net_FontSize", 120.0f ) ;
		Globals.set("Net_Euclidean_NodeRadius", 32.0);
		Globals.set("Net_Euclidean_CostFactor", 0.0001);		
		Globals.set("Net_Euclidean_DisplayNodeLabels", true);
		Globals.set("Net_Euclidean_MinimumNodeDistance", 120.0);

		Globals.set("Traffic_DisplayDeadPackets", true ) ;		
		Globals.get("Traffic_DeadPacketHistory", 0.1) ;
		
		new Main().init().start() ;
	}

	public void display(Graphics2D gu, Graphics2D gs) {
		for( Node i : NodeDB.nodes )
			i.display(gu, gs) ;		
	}
	
}
