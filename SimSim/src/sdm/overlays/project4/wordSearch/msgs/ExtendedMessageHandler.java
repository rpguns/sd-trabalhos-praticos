package sdm.overlays.project4.wordSearch.msgs;

import simsim.core.*;

public interface ExtendedMessageHandler extends MessageHandler {
	
	public void onReceive( EndPoint src, ChordMessage m) ;	
}
