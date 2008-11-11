package sdm.overlays.project4.expressionSearch_Traveler.msgs;

import simsim.core.*;

public interface ExtendedMessageHandler extends MessageHandler {
	
	public void onReceive( EndPoint src, PutMessage m) ;
	public void onReceive( EndPoint src, TravelMessage m);
	public void onReceive( EndPoint src, CircularMessage m);
	public void onReceive( EndPoint src, ReplyMessage m);

}
