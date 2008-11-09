package sdm.overlays.project3.expressionSearch;

import java.awt.*;
import java.util.*;

import simsim.core.*;
import simsim.ext.charts.XYLineChart;
import sdm.overlays.project3.expressionSearch.msgs.QueryReply;
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
	public Hashtable<Pair<String,String>,Integer> wordCount = new Hashtable<Pair<String,String>,Integer>(500);
	public static Random generator = new Random();


	public static String generateRegularExpression(Word word) {
		String wordValue = word.value;
		int breakingPoint = generator.nextInt(wordValue.length()-2)+1;
		
		if (generator.nextBoolean()) {
			String basePattern = wordValue.substring(0,breakingPoint);
			return basePattern+".*";
		} else {
			String basePattern = wordValue.substring(breakingPoint);
			return ".*"+basePattern;
		}
	}
	
	Main() {
		super( 10, EnumSet.of( DisplayFlags.SIMULATION, DisplayFlags.TIME, DisplayFlags.NETWORK, DisplayFlags.TRAFFIC ) ) ;
	}

	Main init() {
		WordsDB.init() ;
		
		
		
		final XYLineChart chart1 = new XYLineChart("Query Success", 125.0, "Answered Queries (%)", "time(s)") ;
		chart1.setYRange( false, 0, 100 ) ;
		chart1.setSeriesLinesAndShapes("s0", true, true) ;
		
		final XYLineChart chart2 = new XYLineChart("Messages Sent", 125.0, "Messages Sent", "time(s)") ;
		chart2.setYRange( false, 0, 30000000 ) ;
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


			
//		Word testWord = WordsDB.randomWord();
//		Node testNode = NodeDB.randomNode();
		//testNode.query(testWord,wordCount.get(testWord));
		super.setSimulationMaxTimeWarp(5.0) ;

		//System.out.println("Test word exists in "+ wordCount.get(testWord)+" nodes");
		System.out.println("Init complete...") ;
		
		new PeriodicTask(0.5) {
			public void run() {
				
				if (sentQueries < NUM_OF_QUERIES) {
					Node n = NodeDB.randomNode();
					String pattern1 = generateRegularExpression(n.words.randomElement());
					String pattern2 = generateRegularExpression(n.words.randomElement());
//					System.out.println("Expression 1: "+pattern1+"\tExpression 2: "+pattern2);

					for( Node i : NodeDB.nodes() ) {
						Pair<Pair<Word,String>,Pair<Word,String>> matchingResults = 
							i.patternizer(pattern1,pattern2);

						if (matchingResults != null) {
//							System.out.println("Node: "+i.address.pos);
//							System.out.println(matchingResults.first.first.value+ " == "+ pattern1);
//							System.out.println(matchingResults.second.first.value+ " == "+ pattern2);
//						
							Integer previous = wordCount.get(new Pair<String,String>(pattern1,pattern2));
							if (previous == null)
								wordCount.put(new Pair<String,String>(pattern1,pattern2),1);
							else
								wordCount.put(new Pair<String,String>(pattern1,pattern2),previous+1);
						}
						//else
							//System.err.println("WE R GOTS A NULL");
					}	
					
					NodeDB.randomNode().query(pattern1,pattern2);
					sentQueries++;
					

					//System.out.println("expected result: "+ wordCount.get(new Pair<String,String>(pattern1,pattern2)));
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
				int j = 0;
				for( Node n : NodeDB.nodes() ) {
					j += n.messagesSent;
					if (n.startedQuery) {
						for (Pair<String,String> queriedExp:n.myQueryAnswers.keySet()) {
								if (n.myQueryAnswers.get(queriedExp) != null) {
									
									if (n.myQueryAnswers.get(queriedExp) != null) { 
										//System.out.println(n.myQueryAnswers.get(queriedExp).size() +" < "+wordCount.get(queriedExp));
										if (n.myQueryAnswers.get(queriedExp).size() < wordCount.get(queriedExp))
											i++;
									}
								}
						}
					}
					};
				System.err.println(sentQueries);
				System.out.println(sentQueries-i +" done with "+sentQueries+" sent");
				chart1.getSeries("s0").add( Simulation.currentTime(),  ((sentQueries-i)*100)/(sentQueries)) ;
				chart2.getSeries("s0").add( Simulation.currentTime(),  j) ;
				
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
