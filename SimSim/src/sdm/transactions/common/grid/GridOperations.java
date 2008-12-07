package sdm.transactions.common.grid;

import simsim.ext.rmi.*;

public interface GridOperations extends Remote {
	
	public int[] gridSize( long tid ) ;
	
	public int readColor( long tid, int i, int j ) ;
	
	public void writeColor( long tid, int i, int j, int v ) ;
	
	public int readShape( long tid, int i, int j ) ;
	
	public void writeShape( long tid, int i, int j, int v ) ;
	
}
