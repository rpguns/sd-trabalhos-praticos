package simsim.core;

import java.util.* ;

/**
 * This class provides basic functionality for joining, leaving and sending messages to groups of nodes. 
 * Groups are identified by an object name, usually a string. They are created on demand by the act of 
 * joining or requesting its endpoint.
 * 
 * Joining a group is only needed for receiving messages addressed to the group.
 * 
 * @author  Sérgio Duarte (smd@di.fct.unl.pt)
 *
 */
public class Multicasting {
		
	/**
	 * Returns a endpoint that can be used to send multicast messages to a group of nodes.
	 * If the group does not yet exist it is created.
	 * @param group - the name of the group, usually a string.
	 * @return the endpoint associated with the multicast group.
	 */
	public static EndPoint endpoint( Object group ) {	
		GroupDispatcher o = groups.get( group ) ;
		if( o == null ) 
			groups.put(group, o = new GroupDispatcher()) ; 

		return o.endpoint ;
	}
	
	/**
	 * Allows a node to join a group and receive messages via the handler associated with an endpoint.
	 * If the group does not yet exist it is created.
	 * @param group - the name of the group, usually a string.
	 * @param e - the endpoint that will be used to deliver messages to the node.
	 * @return Returns the endpoint of the multicast group, which can be used to send messages to the group.
	 */
	public static EndPoint joinGroup( Object group, EndPoint e ) {
		GroupDispatcher o = groups.get( group ) ;
		if( o == null ) 
			groups.put(group, o = new GroupDispatcher()) ; 
		
		o.members.add( e ) ;
		return o.endpoint ;
	}
	
	/**
	 * Allows a node to remove itself from a multicast group.
	 * @param group - the name of the group, usually a string.
	 * @param e - the endpoint that had joined the group.
	 */
	public static void leaveGroup( Object group, EndPoint e ) {
		GroupDispatcher o = groups.get( group ) ;
		if( o != null ) 
			o.members.remove( e ) ;
	}

	static void leaveAllGroups( EndPoint e ) {
		for( GroupDispatcher i : groups.values() )
			i.members.remove(e ) ;
	}
	
	static class GroupDispatcher implements MessageHandler {
		EndPoint endpoint ;
		HashSet<EndPoint> members = new HashSet<EndPoint>() ;

		protected GroupDispatcher() {
			endpoint = address.endpoint( groups.size() + 1, this ) ;
			endpoint.setMulticastFlag( true ) ;
		}


		public void onReceive( EndPoint src, Message m) {
			for( Iterator<EndPoint> i = members.iterator() ; i.hasNext() ; )
				if( !src.udpSend( i.next(), m) )
					i.remove();
		}

		public void onReceive(TcpChannel chn, Message m) {
		}

		public void onSendFailure(EndPoint dst, Message m) {
		}
	}
	
	static MulticastGroupAddress address = new MulticastGroupAddress() ;
	static Map<Object, GroupDispatcher> groups = new HashMap<Object, GroupDispatcher>() ;
}

class MulticastGroupAddress extends NetAddress {

	protected MulticastGroupAddress() {
		super(null);
	}

	public NetAddress replace() {
		return this;
	}
}
