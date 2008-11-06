package sdm.epidemic.examples.A.msgs;

import simsim.core.*;

/**
 * Extends the default message handler of node with new message subtypes...
 * 
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 *
 */
public interface ExtendedMessageHandler extends MessageHandler {

	public void onReceive( EndPoint src, InfectNode m) ;	
		
}
