package sdm.time.project6.election.msgs;

import simsim.core.*;

public interface MiddlewareMessageHandler extends MessageHandler {
	
	public void onReceive( EndPoint src, FifoOrderMulticast m) ;

	public void onReceive( EndPoint src, TotalOrderMulticast m) ;

}
