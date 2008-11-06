package sdm.overlays.unstructured;

import java.awt.*;
import java.util.*;

import simsim.core.*;
import sdm.overlays.words.*;
/**
 * This example shows how to create a simulation with a dynamic population of nodes.
 * 
 * At the beginning, a certain number of nodes is created and then initialized.
 * 
 * A periodic task is used to increase the node population, while another task
 * removes nodes from the system at random, simulating abrupt failures.
 *
 * Each node starts with a seed endpoint that allows it begin ping other nodes.
 * When a PingNode message is received, the source endpoint is stored for future use.
 * Each node is allowed to store a small number of endpoints and so is forced to
 * discard old ones to meet this requirement.
 *  
 *
 * @author smd
 *
 */
public class Main extends Simulation implements Displayable {
	
	public static final int TOTAL_NODES = 2000 ;

	Main() {
		super( 10, EnumSet.of( DisplayFlags.SIMULATION, DisplayFlags.TIME, DisplayFlags.NETWORK, DisplayFlags.TRAFFIC ) ) ;
	}

	Main init() {
		WordsDB.init() ;
				
		//Create the simulation nodes
		for( int i = 0 ; i < TOTAL_NODES ; i++ ) 
			new Node() ;
	
		//Initialize the simulation nodes
		for( Node i : NodeDB.nodes() ) 
			i.init() ;
				
		super.setSimulationMaxTimeWarp(1e8) ;
		
		
		System.out.println("Init complete...") ;
		return this ;
	}
	
	public void display( Graphics2D gu, Graphics2D gs ) {
		gs.setColor( Color.gray ) ;
		gs.setStroke( new BasicStroke(0.5f)) ;

		for( Node i : NodeDB.nodes() ) 
			i.display(gu, gs) ;
	}
	
	public static void main( String[] args ) throws Exception {

		Globals.set("Sim_RandomSeed", 0L);
		Globals.set("Net_RandomSeed", 0L);
		
		Globals.set("Net_Euclidean_NodeRadius", 8.0);
		Globals.set("Net_Euclidean_DisplayNodeLabels", false);
		Globals.set("Net_Euclidean_MinimumNodeDistance", 15.0);
		
		new Main().init().start() ;
	}
}
