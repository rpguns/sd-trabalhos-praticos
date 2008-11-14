package sdm.overlays.project4.dynamic.msgs;

import simsim.core.*;

public interface ExtendedMessageHandler extends MessageHandler {
	
	public void onReceive( EndPoint src, GiefSuccessor m);
	public void onReceive( EndPoint src, HereIsYourSuccessor m);

}
