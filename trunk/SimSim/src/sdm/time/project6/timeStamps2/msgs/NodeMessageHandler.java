package sdm.time.project6.timeStamps2.msgs;

import simsim.core.*;

public interface NodeMessageHandler extends MessageHandler {
		
	public void onReceive( EndPoint src, SyncTimeReply m) ;	
	
	public void onReceive( EndPoint src, SyncTimeRequest m) ;	

}
