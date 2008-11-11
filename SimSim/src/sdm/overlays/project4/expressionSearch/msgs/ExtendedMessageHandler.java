package sdm.overlays.project4.expressionSearch.msgs;

import simsim.core.*;

public interface ExtendedMessageHandler extends MessageHandler {
	
	//public void onReceive( EndPoint src, ChordMessage m) ;	
	public void onReceive( EndPoint src, PutMessage m) ;
	public void onReceive( EndPoint src, GetMessage m) ;
	public void onReceive( EndPoint src, GetReply m) ;
}
