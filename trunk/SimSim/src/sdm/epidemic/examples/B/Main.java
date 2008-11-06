package sdm.epidemic.examples.B;

import java.util.*;

import simsim.core.*;
import simsim.ext.charts.*;

/**
 * This example shows how to create a simulation with a static population of nodes.
 * 
 * At the beginning, a certain number of nodes is created and then initialized.
 * 
 * Each node starts with a number of seed endpoints that allows it to communicate with other nodes.
 * Each node is only allowed to store a small number of endpoints, so it must discard old ones to meet this requirement.
 *  
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 *
 */
public class Main extends Simulation {
	
	public static final int TOTAL_NODES = 2000 ;

	Main() {
		super( 20, EnumSet.of( DisplayFlags.SIMULATION, DisplayFlags.TIME, DisplayFlags.NETWORK, DisplayFlags.TRAFFIC ) ) ;
	}

	Main init() {
		
		// Create a chart to monitor the infection progress rate
		final XYLineChart chart = new XYLineChart("Infection Rate", 5.0, "Infected Nodes (%)", "time(s)") ;
		chart.setYRange( false, 0, 100 ) ;
		chart.setSeriesLinesAndShapes("s0", true, true) ;
		
		Gui.setFrameRectangle("MainFrame", 0, 0, 480, 480);
		Gui.setFrameRectangle("Infection Rate", 484, 0, 480, 480);

		//Create the simulation nodes
		for( int i = 0 ; i < TOTAL_NODES ; i++ ) new Node() ;

		
		//Initialize the simulation nodes
		for( Node i : NodeDB.nodes() ) i.init() ;
				
		
		NodeDB.randomNode().infect() ;
		
		// Sets up a periodic task that, at one second intervals, computes and shows the percentage of infected nodes in the system
		// Stops the simulation when it detects that every node is infected...
		new PeriodicTask(1.0) {
			public void run() {
				double T = 0, N = 0 ;
				for( Node n : NodeDB.nodes() ) {
					if( n.infected ) T++ ;
					N++ ;
				}
				chart.getSeries("s0").add( Simulation.currentTime(),  100.0 * T / N ) ;
				if( N == T ) 
					stop() ;
			} ;
		};
				
		super.setSimulationMaxTimeWarp(5) ;
		
		return this ;
	}

	public static void main( String[] args ) throws Exception {
		Globals.set("Sim_RandomSeed", 0L);
		Globals.set("Net_RandomSeed", 1L);
		
		Globals.set("Net_Euclidean_NodeRadius", 6.0);
		Globals.set("Net_Euclidean_MinimumNodeDistance", 20.0);

		new Main().init().start() ;
	}	
}
