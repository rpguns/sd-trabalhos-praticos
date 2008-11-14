package sdm.overlays.project3.expressionSearch.msgs;

import simsim.core.*;

public interface ExtendedMessageHandler extends MessageHandler {
	
	public void onReceive(EndPoint src, SearchQuery m);
	
	public void onReceive(EndPoint src, QueryReply m);
	
	public void onReceive(EndPoint src, ContactExchange m);
	
}
