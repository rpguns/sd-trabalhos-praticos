package sdm.transactions.common;

import java.awt.*;

import simsim.core.*;

public class Node extends AbstractNode implements Displayable {

	public int key ;	
	public Node( Color color ) {
		super();
		super.setColor( color ) ;
		key = NodeDB.store( this) ;
	}
	
	public void init() {
	}

	public void display( Graphics2D gu, Graphics2D gs) {
	}
	
    public String toString() {
    	return Long.toString( key) ;
    }
}
