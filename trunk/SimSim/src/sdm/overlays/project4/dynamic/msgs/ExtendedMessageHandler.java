package sdm.overlays.project4.dynamic.msgs;

import simsim.core.*;

public interface ExtendedMessageHandler extends MessageHandler {
	
	public void onReceive( EndPoint src, GiefSuccessor m);
	public void onReceive( EndPoint src, HereIsYourSuccessor m);
	public void onReceive( EndPoint src, HereIsMyPredecessor m);
	public void onReceive( EndPoint src, GiefPredecessor m);
	public void onReceive( EndPoint src, NotifyMessage m);
	public void onReceive( EndPoint src, LookupMessage m);
	public void onReceive( EndPoint src, LookupReply m);
	public void onReceive( EndPoint src, Ping m);
	public void onSendFailure( EndPoint src, Ping m);
}
