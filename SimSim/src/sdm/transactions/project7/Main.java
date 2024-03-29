package sdm.transactions.project7;

import java.awt.*;
import java.util.*;

import sdm.transactions.common.*;
import simsim.core.*;
import simsim.ext.charts.XYLineChart;

public class Main extends Simulation implements Displayable {
	
	public static final int TOTAL_SERVERS = 1 ; 
	public static final int TOTAL_CLIENTS = 3 ;

	Main() { 
		super( 20, EnumSet.of( DisplayFlags.SIMULATION, DisplayFlags.TIME, DisplayFlags.THREADS, DisplayFlags.NETWORK, DisplayFlags.TRAFFIC )) ;
	}

	Main init() {
		final XYLineChart chart = new XYLineChart("Transaction success percentage", 5.0, "transaction success (%)", "time(s)") ;
		chart.setSeriesLinesAndShapes("s0", true, false) ;
		
		Gui.setFrameRectangle("MainFrame", 0, 0, 480, 480);		
		Gui.setFrameRectangle("Transaction success percentage", 484, 0, 480, 480) ;
		
		for( int i = 0 ; i < TOTAL_SERVERS ; i++ )
			new Server() ;
		
		for( int i = 0 ; i < TOTAL_CLIENTS ; i++ )
			new Client();
		
		//Initialize the simulation nodes (includes servers and clients)
		for( Node i : NodeDB.nodes() ) 
			i.init() ;
		
		//Graph Drawing
		new PeriodicTask( 30, 5) {
			public void run() {
				//Only one server active so this obviously works
				Server standaloneServer = (Server)ServerDB.randomServer();
				double successRatio = 
					standaloneServer.numberOfCommitedTransactions/standaloneServer.numberOfClosedTransactions;
				chart.getSeries("s0").add( currentTime(), 100*successRatio ) ;
			}
		};
		
		super.setSimulationMaxTimeWarp( 50 ) ;

		System.out.println("Init complete...") ;
		return this ;
	}
	
	public void display(Graphics2D gu, Graphics2D gs) {
		for( Node i : NodeDB.nodes() )
			i.display( gu, gs) ;		
	}

	public static void main( String[] args ) throws Exception {
		
		Globals.set("Sim_RandomSeed", 1L);
		Globals.set("Net_RandomSeed", 1L);

		Globals.set("Net_FontSize", 24.0f);
		
		Globals.set("Net_ToggleDrawNodeMode", true ) ;
		Globals.set("Net_Euclidean_MinimumNodeDistance", 120.0);
		Globals.set("Net_Euclidean_DisplayNodeLabels", true);
		Globals.set("Net_Euclidean_NodeRadius", 30.0);
		Globals.set("Net_Euclidean_CostFactor", 0.00001);	
		
		new Main().init().start() ;
	}

}
