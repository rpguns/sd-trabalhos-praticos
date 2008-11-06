package sdm.epidemic.examples.A;

import java.util.*;

import simsim.core.*;
import simsim.ext.charts.*;

/**
 * This simple example shows how to create a simulation.
 * 
 * In this simulation, a certain number of nodes is created and then initialized.
 * 
 * The nodes never go offline, so the size of the system remains constant...
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 * 
 */

public class Main extends Simulation {

	public static final int TOTAL_NODES = 2000;

	Main() {
		// Setup the MainFrame to be updated at a maximum of 20 frame/s, showing the listed displayable items.
		super(20, EnumSet.of(DisplayFlags.SIMULATION, DisplayFlags.TIME, DisplayFlags.NETWORK, DisplayFlags.TRAFFIC));
	}

	Main init() {
		
		// Set up and parameterize the GUI.		
		// Set the position and size of the default simulator window/frame: "MainFrame"
		Gui.setFrameRectangle("MainFrame", 0, 0, 480, 480);		

		// Create a chart to monitor the infection progress rate
		final XYLineChart chart = new XYLineChart("Infection Rate", 5.0, "Infected Nodes (%)", "time(s)") ;
		chart.setYRange( false, 0, 100 ) ;
		chart.setSeriesLinesAndShapes("s0", true, true) ;
		
		// Set the position and size of the Infection Rate frame.
		Gui.setFrameRectangle("Infection Rate", 484, 0, 480, 480);

		// Create the simulation nodes
		for (int i = 0; i < TOTAL_NODES; i++)
			new Node();

		// Initialize the simulation nodes
		for (Node i : NodeDB.nodes())
			i.init();

		// Select random node to be the origin of the infection.
		NodeDB.randomNode().infect() ;

		// Sets up a periodic task that, at one second intervals, computes and
		// shows the percentage of infected nodes in the system
		// Stops the simulation when it detects that every node is infected...
		new PeriodicTask(1.0) {
			public void run() {
				double T = 0, N = 0;
				for (Node n : NodeDB.nodes()) {
					if (n.infected)
						T++;
					N++;
				}

				chart.getSeries("s0").add( Simulation.currentTime(), 100.0 * T / N ) ;
				if (N == T)
					stop();				
			};
		};

		// Set Simulation time speed to 50% of real time.
		super.setSimulationMaxTimeWarp(5);

		return this;
	}

	public static void main(String[] args) throws Exception {

		// Initialize a few of the global variables that control the behavior of the simulator...
		
		// Sets the two random generator seeds used in the simulator.
		// A value different than 0L will produce always the same reproducible sequence of random numbers.
		Globals.set("Sim_RandomSeed", 0L);
		Globals.set("Net_RandomSeed", 1L);

		Globals.set("Net_Euclidean_NodeRadius", 6.0);
		Globals.set("Net_Euclidean_MinimumNodeDistance", 20.0);

		new Main().init().start();
	}

}