package sdm.overlays.project4.dynamic.msgs;

import simsim.core.*;

public class LookupReply extends HereIsYourSuccessor {
	
	protected int fingerNumber;
	
	public LookupReply( double key, EndPoint succ, int nFinger ) {
		super( key,succ );
		this.fingerNumber = nFinger;
	}
	
	public int getFingerNumber() {
		return fingerNumber;
	}
	
}
