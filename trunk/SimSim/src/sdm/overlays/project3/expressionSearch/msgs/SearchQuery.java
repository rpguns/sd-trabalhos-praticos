package sdm.overlays.project3.expressionSearch.msgs;

import java.awt.* ;
import simsim.core.*;

public class SearchQuery extends Message {
	
	protected EndPoint origin;
	protected String exp1,exp2;
	
	protected int ID;
	protected int TTL;
	
	public SearchQuery( EndPoint origin, String exp1, String exp2, int ID, int TTL ) {
		super( false, Color.red ) ;
		
		this.origin = origin;
		this.exp1 = exp1;
		this.exp2 = exp2;
		this.ID = ID;
		this.TTL = TTL;
	}
	
	public EndPoint getOrigin() {
		return origin;
	}
	
	public String getExpression1() {
		return exp1;
	}
	
	public String getExpression2() {
		return exp2;
	}
	
	public int getID() {
		return ID;
	}
	
	public int getTTL() {
		return TTL;
	}

//	public int compareTo(SearchQuery o) {
//		if (this.ID == ((SearchQuery)o).getID() && this.origin  == ((SearchQuery)o).getOrigin())
//			return 0;
//		else if (this.ID > ((SearchQuery)o).getID() && this.origin  == ((SearchQuery)o).getOrigin())
//			return -1;
//		return 1;
//	}
	
	public boolean equals(Object o) {
		if (this.ID == ((SearchQuery)o).getID() && this.origin  == ((SearchQuery)o).getOrigin())
			return true;
		return false;
	}
	
	public void deliverTo( EndPoint src, MessageHandler handler ) {
		((ExtendedMessageHandler)handler).onReceive( src, this ) ;
	}
}
