package sdm.time.physical.msgs;

import simsim.core.*;

public interface ExtendedMessageHandler extends MessageHandler {
	
	public void onReceive( EndPoint src, SyncTimeRequest m) ;	
	
	public void onReceive( EndPoint src, SyncTimeReply m) ;	
	
}
