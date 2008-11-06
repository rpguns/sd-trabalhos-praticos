package sdm.epidemic.examples.C.msgs;

import simsim.core.*;

public interface ExtendedMessageHandler extends MessageHandler {
		
	public void onReceive( EndPoint src, InfectNode m) ;	
	
	public void onReceive( EndPoint src, SeedExchange m) ;	
}
