package sdm.epidemic.examples.A;

import java.awt.*;


import sdm.epidemic.examples.A.msgs.*;
import simsim.core.*;
import static simsim.core.Simulation.* ;


/**
 * This class is used to simulate an autonomous node of a Distributed System.
 * 
 * Each node acts independently and schedules tasks to simulate parallel execution.
 * <b>IMPORTANT</b>. You must not use threads; only tasks and periodic tasks can be used to simulate concurrency in the simulator.
 * 
 * In this example, all nodes but one start healthy; the infected node is chosen at random, at the beginning of the simulation. 
 * 
 * In every node, a periodic task executes every second. In healthy nodes, this task does nothing until they become infected. When that happens, 
 * every time the task is executed, the node will send an InfectNode message to another node, chosen randomly, to infect it. 
 * This is more than enough to cause the infection to spread quickly.
 * 
 * Healthy nodes in the system are represented as green dots in the Main simulator window.
 * Infected nodes are represented in red. 
 * 
 * Every time the simulation window is updated, the simulator also draws the message traffic observed in that period.
 *
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 *
 */
public class Node extends AbstractNode implements ExtendedMessageHandler {

	/**
	 * Tells if a node is healthy or infected. Nodes start healthy, except one chosen at random at the beginning of the simulation.
	 */
	boolean infected = false ;
		

	/**
	 * Creates a new node and stores it in a global node database.
	 */
	public Node() {
		super() ;
		NodeDB.store(this) ;
	}

	/**
	 * Put the node in infected state
	 */
	public void infect() {
		infected = true ;
		setColor( Color.RED ) ;
	}
	
	/* (non-Javadoc)
	 * 
	 * Initializes the node state.
	 * Creates the periodic task that infected nodes use to pass on the infection.
	 * 
	 * Healthy nodes remain silent until infected.
	 * 
	 * @see sim.core.AbstractNode#init()
	 */
	public void init() {

		setColor( Color.GREEN ) ;

		new PeriodicTask( this, rg.nextDouble(), 1.0) {
			public void run() {
				if( infected ) 
					udpSend( NodeDB.randomNode().endpoint, new InfectNode()  ) ;		
			}
		};
	}
	
	/* (non-Javadoc)
	 * 
	 * Handler for an incoming InfectNode message
	 * 
	 * When this message is delivered to a node, it becomes infected and its color
	 * is changed to red.
	 * 
	 * Next time its periodic task executes, it will try to infect another node.
	 * 
	 * @see examples.A.msgs.ExtendedMessageHandler#onReceive(sim.net.EndPoint, examples.A.msgs.InfectNode)
	 */
	public void onReceive(EndPoint src, InfectNode m) {
		this.infect() ;
	}	
}