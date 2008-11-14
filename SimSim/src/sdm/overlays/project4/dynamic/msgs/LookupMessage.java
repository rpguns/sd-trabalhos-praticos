package sdm.overlays.project4.dynamic.msgs;

import simsim.core.*;

public class LookupMessage extends GiefSuccessor {
	
	public LookupMessage( EndPoint src, double key ) {
		super(src,key);
	}
	
	public LookupMessage( GiefSuccessor other ) {
		super(other);
	}
	
}
