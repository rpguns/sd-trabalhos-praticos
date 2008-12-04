package sdm.time.project6.timeStamps.msgs;

import simsim.core.*;

public interface ProcessMessageHandler extends MessageHandler {
	
	public void onReceive( EndPoint src, ShapeOperation m) ;
	
	public void onReceive( EndPoint src, ChangeColor m) ;
	
}
