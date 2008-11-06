package simsim.ext.rmi;

import java.awt.Color;
import java.lang.reflect.*;

import simsim.core.* ;

/**
 * An internal class of the SimSim RMI/RPC package that is used to encode the invocation data associated with a RMI/RPC call to a remote object.
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 */
class RmiInvocation extends Message {
	
	Method method ;
	Object[] args ;
	EndPoint client ;
	
	RmiInvocation( EndPoint client, Method m, Object[] args ) {
		super( visibleRmiMessages, rmiMessageInvocationColor ) ;
		this.method = m ;
		this.args = args ;
		this.client = client ;
	}	
	
	/* (non-Javadoc)
	 * <b>IMPORTANT!!!</b> Include a copy of this method in every class that extends Message
	 * @see sim.core.Message#deliverTo(sim.net.TcpChannel, sim.core.MessageHandler)
	 */
	public void deliverTo( TcpChannel ch, MessageHandler handler ) {
		((UnicastRemoteObject)handler).onReceive( ch, this ) ;
	}

	private static boolean visibleRmiMessages = Globals.get("Rmi_VisibleMessages", false ) ;
	private static Color rmiMessageInvocationColor = Globals.get("Rmi_MsgInvocationColor", Color.BLACK ) ;
}
