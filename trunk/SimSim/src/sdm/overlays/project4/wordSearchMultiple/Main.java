package sdm.overlays.project4.wordSearchMultiple;

import java.awt.*;
import java.util.*;


import simsim.core.*;
import simsim.ext.charts.XYLineChart;
import simsim.utils.*;

import simsim.gui.geom.*;
import sdm.overlays.words.*;


public class Main extends Simulation implements Displayable {
	
	public static final int TOTAL_NODES = 2000 ;
	public static final int NUM_OF_QUERIES = 500 ;
	int sentQueries = 0;
	
	Main() {
		super( 10, EnumSet.of( DisplayFlags.SIMULATION, DisplayFlags.TIME, DisplayFlags.TRAFFIC ) ) ;
	}

	Main init() {
		super.setSimulationMaxTimeWarp(2.0) ;
		
		Gui.setFrameRectangle("MainFrame", 0, 0, 640, 640);
		
		WordsDB.init() ;
		
		final XYLineChart chart1 = new XYLineChart("Query Success", 125.0, "Query Success", "time(s)") ;
		chart1.setYRange( false, 0, 100 ) ;

		
		final XYLineChart chart2 = new XYLineChart("Messages Sent", 125.0, "Messages Sent", "time(s)") ;
		chart2.setYRange( false, 0, 1000000 ) ;
		
		chart1.setSeriesLinesAndShapes("s0", true, true) ;
		chart2.setSeriesLinesAndShapes("s0", true, true) ;
		Gui.setFrameRectangle("MainFrame", 0, 0, 360, 360);
		Gui.setFrameRectangle("Messages Sent", 364, 0, 360, 360);
		Gui.setFrameRectangle("Query Success", 0, 364, 360, 360);
		
		
		//Create the simulation nodes
		for( int i = 0 ; i < TOTAL_NODES ; i++ ) 
			new Node() ;

		
		//Initialize the simulation nodes
		for( Node i : NodeDB.nodes() ) 
			i.init() ;
		
		//Test of Chord recursive routing to a known node...
		/*new PeriodicTask(0.1) {
			public void run() {
				NodeDB.randomNode().routeTo( NodeDB.randomNode().chordKey ) ;
			}
		};*/

		new PeriodicTask(1.0) {
			public void run() {
				if (sentQueries < NUM_OF_QUERIES) {
					NodeDB.randomNode().query( WordsDB.randomWord() ) ;
					sentQueries++;
					reSchedule( 0.5 + (1.5 * rg.nextDouble() )) ; //schedules a new execution of this task...
				}
			}
		};
		
		new PeriodicTask(10.0) {
			public void run() {
				int i = 0;
				int j = 0;
				for( Node n : NodeDB.nodes() ) {
					i += n.answeredQueries;
					j += n.messagesSent;
				}
				System.err.println("Sent "+sentQueries+" received "+i);
				chart1.getSeries("s0").add( Simulation.currentTime(),  (i*100/sentQueries)) ;
				chart2.getSeries("s0").add( Simulation.currentTime(),  j) ;
				
			}
		};

		System.out.println("Init complete...") ;
		return this ;
	}
	
	public static void main( String[] args ) throws Exception {
		Globals.set("Sim_RandomSeed", 0L);
		Globals.set("Net_RandomSeed", 1L);
		
		Globals.set("Net_Euclidean_NodeRadius", 10.0);
		Globals.set("Net_Euclidean_CostFactor", 0.0001);		
		Globals.set("Net_Euclidean_DisplayNodeLabels", false);
		Globals.set("Net_Euclidean_MinimumNodeDistance", 50.0);
		
		Globals.set("Traffic_DeadPacketHistory", 0.5) ;
		Globals.set("Traffic_DisplayDeadPackets", true ) ;
		Globals.set("Traffic_DisplayDeadPacketsHistory", "time" ) ;
		new Main().init().start() ;
	}

	public void display(Graphics2D gu, Graphics2D gs) {

		// Find closest node to the mouse pointer
		XY mouse = Gui.getMouseXY_Scaled( gs) ;
		Node closest = null ;
		for (Node i : NodeDB.nodes() )
			if (closest == null || mouse.distance( i.pos) < mouse.distance( closest.pos))
				closest = i;

		// If mouse pointer is close to a node, then show its routing table
		if( closest != null && mouse.distance( closest.pos ) < 9.0 ) {
			final XY radius = new XY(20,20) ;
			gs.setColor( Color.red ) ;
			gs.fill( new Ellipse( closest.pos, radius )) ;			
			closest.rtable.display( gu, gs ) ;
		}
		
		// Display the nodes
		gs.setColor( Color.black ) ;
		gs.setStroke(new BasicStroke(1f));
		for( Node i : new ArrayList<Node>(NodeDB.nodes()) )
			i.display(gu, gs) ;
		
	}
}
