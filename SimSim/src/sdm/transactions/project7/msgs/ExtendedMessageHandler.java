package sdm.transactions.project7.msgs;

import simsim.core.*;

public interface ExtendedMessageHandler extends MessageHandler {
	
	public void onReceive( EndPoint src, PingResponse m) ;	
	
	public void onReceive( EndPoint src, PingClient m) ;
}
