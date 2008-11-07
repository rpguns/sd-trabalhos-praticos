package sdm.overlays.project3.expressionSearch.msgs;

import java.awt.* ;
import simsim.core.*;
import sdm.overlays.words.*;
import sdm.overlays.project3.expressionSearch.*;

public class QueryReply extends Message {
	
	protected Pair<Word,String> matchingWord1,matchingWord2;
	protected int ID;
	
	public QueryReply( Pair<Word,String> mw1, Pair<Word,String> mw2, int ID) {
		super( false, Color.red ) ;

		this.matchingWord1 = mw1;
		this.matchingWord2 = mw2;
		this.ID = ID;
	}
	
	public Pair<Word,String> getMatchingWord1() {
		return matchingWord1;
	}
	
	public Pair<Word,String> getMatchingWord2() {
		return matchingWord2;
	}
	
	public int getID() {
		return ID;
	}

	public void deliverTo( EndPoint src, MessageHandler handler ) {
		((ExtendedMessageHandler)handler).onReceive( src, this ) ;
	}
}
