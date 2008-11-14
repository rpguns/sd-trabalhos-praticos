package sdm.overlays.project4.dynamic.msgs;

import simsim.core.*;

public class LookupMessage extends GiefSuccessor {
	
	protected int fingerNumber;
	
	public LookupMessage( EndPoint src, double key, int nFinger ) {
		super(src,key);
		this.fingerNumber = nFinger;
	}
	
	public LookupMessage( LookupMessage other ) {
		super(other);
		this.fingerNumber = other.getFingerNumber();
	}
	
	public int getFingerNumber() {
		return fingerNumber;
	}
	
}
