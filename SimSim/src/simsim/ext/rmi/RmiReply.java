package simsim.ext.rmi;

import java.awt.Color;

import simsim.core.* ;

/**
 * An internal class of the SimSim RMI/RPC package that is used to return the result of a RMI/RPC call to a remote object.
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 *
 */
class RmiReply extends Message {
	Object reply ;
	
	RmiReply( Object reply ) {
		super( visibleRmiMessages, rmiMessageReplyColor ) ;
		this.reply = reply ;		
	}
	
	private static boolean visibleRmiMessages = Globals.get("Rmi_VisibleMessages", false ) ;
	private static Color rmiMessageReplyColor = Globals.get("Rmi_MsgInvocationColor", Color.BLUE ) ;
}
