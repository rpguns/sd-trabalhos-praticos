package sdm.overlays.project3.wordSearchMultiple;

import java.awt.*;
import java.util.*;

import simsim.core.*;
import simsim.ext.charts.XYLineChart;
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

	int sentQueries = 0;
	public static final int TOTAL_NODES = 2000 ;
	public static final int NUM_OF_QUERIES = 500 ;
	public Hashtable<Word,Integer> wordCount = new Hashtable<Word,Integer>(5000);

	Main() {
		super( 10, EnumSet.of( DisplayFlags.SIMULATION, DisplayFlags.TIME, DisplayFlags.NETWORK, DisplayFlags.TRAFFIC ) ) ;
	}

	Main init() {
		WordsDB.init() ;
		
		
		
		final XYLineChart chart1 = new XYLineChart("", 125.0, "Unanswered queries", "time(s)") ;
		chart1.setYRange( false, 0, 100 ) ;
		chart1.setSeriesLinesAndShapes("s0", true, true) ;
		chart1.setSeriesLinesAndShapes("s1", true, true) ;
		Gui.setFrameRectangle("MainFrame", 0, 0, 480, 480);
		Gui.setFrameRectangle("Error/StdDev", 484, 0, 480, 480);

		
		
		//Create the simulation nodes
		for( int i = 0 ; i < TOTAL_NODES ; i++ ) 
			new Node() ;

		//Initialize the simulation nodes
		for( Node i : NodeDB.nodes() ) 
			i.init() ;

		for( Node i : NodeDB.nodes() ) {
			for (Word w:i.words) {
				Integer previous = wordCount.get(w);
				if (previous == null)
					wordCount.put(w,1);
				else
					wordCount.put(w,previous+1);
			}
		}	
			
//		Word testWord = WordsDB.randomWord();
//		Node testNode = NodeDB.randomNode();
		//testNode.query(testWord,wordCount.get(testWord));
		super.setSimulationMaxTimeWarp(2.0) ;

		//System.out.println("Test word exists in "+ wordCount.get(testWord)+" nodes");
		System.out.println("Init complete...") ;
		
		new PeriodicTask(0.5) {
			public void run() {
				
				if (sentQueries < NUM_OF_QUERIES) {
					Word query = WordsDB.randomWord();
					NodeDB.randomNode().query(query,wordCount.get(query));
					sentQueries++;
					reSchedule( 0.5 + (1.5 * rg.nextDouble() )) ; //schedules a new execution of this task...
				}
				
			}
		};
		
//		new PeriodicTask(1.0) {
//		public void run() {
//			for( Node n : NodeDB.nodes() ) {
//				if (n.startedQuery)
//					System.out.println("node got "+n.answeredQueries);
//			};
//		}
//	};

		
		new PeriodicTask(10.0) {
			public void run() {
				int i = 0;
				int expected = 0;
				for( Node n : NodeDB.nodes() ) {
					if (n.startedQuery) {
						for (Pair<Word,Integer> queriedWord:n.queriedWords) {
							if (queriedWord != null) {
								if (n.something.get(queriedWord.first) != null) {
									
									if (queriedWord.second != null) { 
										if (n.something.get(queriedWord.first).size() < queriedWord.second)
											i++;
									}
								}	
							}
						}
					}
					};
				System.err.println(sentQueries);
				System.out.println(sentQueries-i +" done with "+sentQueries+" sent");
				chart1.getSeries("s0").add( Simulation.currentTime(),  ((sentQueries-i)*100)/(sentQueries)) ;
			}
		};

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
