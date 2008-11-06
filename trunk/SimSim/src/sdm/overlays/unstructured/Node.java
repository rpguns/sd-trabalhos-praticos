package sdm.overlays.unstructured;

import java.awt.* ;
import java.util.* ;

import simsim.core.*;
import simsim.utils.*;
import simsim.gui.geom.*;

import sdm.overlays.words.*;
import sdm.overlays.unstructured.msgs.*;


public class Node extends AbstractNode implements ExtendedMessageHandler, Displayable {
	private static final int NUM_CONTACTS = 2 ;
	Set<Word> words ;
	RandomList<EndPoint> contacts ;

	public Node() {
		super() ;
		NodeDB.store( this ) ;		
	}
	
	public void init() {
		words = WordsDB.randomWords(10);
		super.setColor( Color.green ) ;
		contacts = NodeDB.randomEndPoints(this, NUM_CONTACTS) ; // número de contactos até 50
		contacts = NodeDB.closestNodes( this, NUM_CONTACTS) ; // obtém X nós na proximidade, útil para fazer debug visual da progressão das pesquisas...
	}

	public void display(Graphics2D gu, Graphics2D gs) {
		for( EndPoint i : contacts )
			gs.draw( new Line( address.pos, i.address.pos ) ) ;
	}

}