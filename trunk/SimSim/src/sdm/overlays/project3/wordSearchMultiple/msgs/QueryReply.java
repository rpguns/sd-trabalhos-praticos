package sdm.overlays.project3.wordSearchMultiple.msgs;

import java.awt.* ;
import simsim.core.*;
import sdm.overlays.words.*;

public class QueryReply extends Message {
	
	protected Word word;
	protected int ID;
	
	public QueryReply( Word word, int ID) {
		super( false, Color.red ) ;

		this.word = word;
		this.ID = ID;
	}
	
	public Word getWord() {
		return word;
	}
	
	public int getID() {
		return ID;
	}

	public void deliverTo( EndPoint src, MessageHandler handler ) {
		((ExtendedMessageHandler)handler).onReceive( src, this ) ;
	}
}
