package sdm.overlays.project3.expressionSearch.msgs;

import java.awt.* ;
import simsim.core.*;
import sdm.overlays.words.*;

public class SearchQuery extends Message {
	
	protected EndPoint origin;
	protected Word word;
	
	protected int ID;
	protected int TTL;
	
	public SearchQuery( EndPoint origin, Word word, int ID, int TTL ) {
		super( false, Color.red ) ;
		
		this.origin = origin;
		this.word = word;
		this.ID = ID;
		this.TTL = TTL;
	}
	
	public EndPoint getOrigin() {
		return origin;
	}
	
	public Word getWord() {
		return word;
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
