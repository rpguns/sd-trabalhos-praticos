package sdm.time.project6.logical.msgs;

import simsim.core.*;

public interface NodeMessageHandler extends MessageHandler {
		
	public void onReceive( EndPoint src, SyncTimeReply m) ;	
	
	public void onReceive( EndPoint src, SyncTimeRequest m) ;	

}
