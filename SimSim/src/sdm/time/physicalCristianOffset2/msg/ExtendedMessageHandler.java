package sdm.time.physicalCristianOffset2.msg;

import simsim.core.*;

public interface ExtendedMessageHandler extends MessageHandler {
	
	public void onReceive( EndPoint src, SyncTimeRequest m) ;	
	
	public void onReceive( EndPoint src, SyncTimeReply m) ;	
	
	public void onReceive( EndPoint src, OffsetMessage m) ;	
	
}