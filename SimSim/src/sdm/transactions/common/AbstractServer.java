package sdm.transactions.common;

import java.awt.*;

import simsim.core.*;
import static simsim.core.Simulation.*;

import sdm.transactions.common.grid.*;
import sdm.transactions.common.persistence.*;
import sdm.transactions.common.transaction.*;


abstract public class AbstractServer extends Node {

	final public int sid ;

	public Grid grid ;
	protected AbstractTransactionManager tm ;
	
	public AbstractServer( int gSize ) {
		super( Color.red );
		grid = new Grid( gSize, gSize ) ;	

		sid = ServerDB.store( this ) ;
		
		Gui.addDisplayable("Grid Transactions", new Displayable() {
			public void display( Graphics2D gu, Graphics2D gs) {
				if( tm != null )
					tm.display(gu, gs) ;
			}
		}, 10.0);		
		Gui.setFrameRectangle("Grid Transactions", 484, 0, 480, 480);	
		Gui.setFrameTransform("Grid Transactions", 1000, 1000, 0.05, true) ;
	}
	
	abstract protected AbstractTransactionManager createManager() ;
	
	public void onReboot() {
		setColor( Color.red ) ;
		System.err.println("############################################################") ;
		System.err.println("Server:" + sid + " is online after crashing and rebooting...") ;
		System.err.println("############################################################") ;

		tm = createManager() ;				
		grid = SafeStorage.load( this, "grid" ) ;
		System.err.println("Loaded old grid from persistent storage...") ;
		
		new PeriodicTask( this, 5 * rg.nextDouble(), 60.0) {
			public void run() {
				System.out.println("Server: " + sid + " [Saving state...]") ;
				SafeStorage.save( AbstractServer.this, "grid", grid ) ;
			}
		} ;

	}

		
	public void crash() {
    	super.putOffline();
    	super.cancelAllTasks() ; 
		System.err.println("############################################################") ;
		System.err.println("Server:" + sid + " is offline because it has crashed...") ;
		System.err.println("############################################################") ;
    	new Task( 20 + 80 * rg.nextDouble()) {
    		public void run() {
    			reboot() ;
    		}
    	} ;
    }
    
	public void init() {
		SafeStorage.save( this, "grid", grid) ;
		this.onReboot();
	}
	
    private void reboot() {
    	address = address.replace() ;
    	this.onReboot();
    }   
    
    public String toString() {
    	return "Server " + sid ;
    }
}
