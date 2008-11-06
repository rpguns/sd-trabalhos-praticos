package simsim.core;

import java.awt.*;
import java.util.*;

import static simsim.core.Simulation.*;
/**
 * 
 * The base class to extend to create new node types with some specific behavior.
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 *
 */
abstract public class AbstractNode implements MessageHandler {
	
	/**
	 * The default endpoint of the node. A node node can use multiple endpoints if needed.
	 * Endpoints are used explicitly to send messages and implicitly when a message is received.
	 * 
	 */
	public EndPoint endpoint ;
	
	/**
	 * The Network address used to create the endpoints that support message exchange operations for the node.
	 * Endpoints are used explicitly to send messages and implicitly when a message is received.
	 * 
	 */
	public NetAddress address ;
		
	/**
	 * Creates a new basic simulation node, capable of receiving and sending messages and running tasks.
	 * By default, nodes will share a system-wide clock managed by the task scheduler.
	 */
	protected AbstractNode() {
		this(false) ;
	}

	/**
	 * Creates a new basic simulation node, capable of receiving and sending messages.
	 * @param ownClock Tells if the node will have its own clock, with a particular drift rate and offset, relative to the system-wide scheduler clock.
	 */
	protected AbstractNode( boolean ownClock ) {
		address = Network.createAddress( this ) ;
		endpoint = address.endpoint ;
		clock = ownClock ? new SoftwareClock( this, address.pos) : null ;
	}
	
	/**
	 *  A convenience method to send messages to other nodes.
	 * @param dst Destination node identified by one of its endpoints associated with its address.
	 * @param m Message to be sent.
	 * @return an indication of the success or failure of the operation.
	 */
	protected boolean udpSend( EndPoint dst, Message m ) {
		return endpoint.udpSend( dst, m ) ;
	}

	/**
	 *  A convenience method to send messages to other nodes.
	 * @param dst Destination node identified by its address.
	 * @param m Message to be sent.
	 * @return an indication of the success or failure of the operation.
	 */
	protected boolean udpSend( NetAddress dst, Message m ) {
		return endpoint.udpSend( dst.endpoint, m ) ;
	}

	/**
	 * A convenience method to send messages to other nodes in synchronous/blocking mode.
	 * @param dst Destination node identified by its address.
	 * @param m Message to be sent.
	 * @return Reference to the connection channel. Null if the destination node is offline.
	 */
	protected TcpChannel tcpSend( EndPoint dst, Message m ) {
		return endpoint.tcpSend( dst, m ) ;
	}

	/**
	 * A convenience method to send messages to other nodes in synchronous/blocking mode.
	 * @param dst Destination node identified by its address.
	 * @param m Message to be sent.
	 * @return Reference to the connection channel. Null if the destination node is offline.
	 */	
	protected TcpChannel tcpSend( NetAddress dst, Message m ) {
		return endpoint.tcpSend( dst.endpoint, m ) ;
	}

	/**
	 * Creates new endpoints associated with the node's address, identified by a port number.
	 * Allows the node to install a different message handler for each separate endpoint.
	 * @param port The port that will be used to identify the new endpoint.
	 * @return The newly created endpoint.
	 */
	public EndPoint endpoint( int port ) {
		return address.endpoint( port ) ;
	}
	
	/**
	 * Puts the node in offline state, unable to send or receive messages. 
	 */
	public void putOffline() {
		address.online = false ;
	}
 	
	/**
	 * Returns the online/offline state of the node.
	 * @return Returns the online/offline state of the node.
	 */
	public boolean isOffline() {
		return ! address.online ;
	}
	
	/**
	 * Returns the online/offline state of the node.
	 * @return Returns the online/offline state of the node.
	 */
	public boolean isOnline() {
		return address.online ;
	}
	/**
	 * Returns the current time in the calling node.
	 * @return The current time in the calling node. If the node does not have its own clock then the system-wide simulation time is returned.
	 */
	public double currentTime() {
		return clock == null ? Simulation.currentTime() : clock.currentTime() ;
	}
	
	/**
	 * Sets the color used to render the node in the simulator window.
	 * @param c
	 */
	public void setColor( Color c ) {
		address.color = c ;
	}
	
	/**
	 * Returns the current color of the node.
	 * @return The current color of the node.
	 */
	public Color getColor() {
		return address.color ;
	}
	
	/* (non-Javadoc)
	 * Called asynchronously when sending a message fails due to the destination being offline.
	 * @see sim.core.MessageHandler#onSendFailure(sim.core.EndPoint, sim.core.Message)
	 */
	public void onSendFailure( EndPoint dst, Message m ) {}

	
	/* (non-Javadoc)
	 * 
	 * Called when a message is received and there is no specific handler for it.
	 * @see sim.core.MessageHandler#onReceive(sim.core.EndPoint, sim.core.Message)
	 */
	public void onReceive( EndPoint src, Message m ) {
		throw new RuntimeException("AbstractNode.onReceive(...): Unhandled Message...") ;
	}
	
	/* (non-Javadoc)
	 * 
	 * Called when a synchronous/blocking message is received and there is no specific handler for it.
	 * @see sim.core.MessageHandler#onReceive(sim.core.TcpChannel, sim.core.Message)
	 */
	public void onReceive( TcpChannel ch, Message m ) {
		throw new RuntimeException("AbstractNode.onReceive(...): Unhandled Message...") ;
	}
	
    /**
     * Method that needs to be overridden.
     * Place all node initialization code here, including the tasks that implement the behavior of the node.
     *  
     */
    public void init() {}

    /**
     * Shutdowns the node. This operation is irreversible.
     * Closes and frees the communication address of this node. No further communication can be sent to or received from this node.
     * Cancels all tasks owned by this node.
     */
    public void dispose() {
    	address.dispose() ;
    	cancelAllTasks() ;  	    			    			
    }
    
    
    /**
     * Registers a task previously issued by this node.
     *      
    */
     void registerNodeTask( Task t ) {
    	nodeTasks.add( t ) ;
     }
    
    /**
     * Cancels all of the "named" tasks issued by this node.
     */
    protected void cancelAllTasks() {
    	for( Task i : nodeTasks ) {
    		i.cancel() ;
    	}
    	nodeTasks.clear();
    }
    
	protected SoftwareClock clock ;
	private Set<Task> nodeTasks = new HashSet<Task>() ;
}
