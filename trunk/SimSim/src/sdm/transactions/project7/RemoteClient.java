package sdm.transactions.project7;

import simsim.ext.rmi.*;

public interface RemoteClient extends Remote {

	public boolean isActive(long tid);
	
	public void crashThyOwner();

}