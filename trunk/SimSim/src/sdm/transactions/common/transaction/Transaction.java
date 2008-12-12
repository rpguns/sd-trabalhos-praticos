package sdm.transactions.common.transaction;

import simsim.ext.rmi.*;
import sdm.transactions.project7.*;
import simsim.core.*;

public interface Transaction extends Remote {
	
	static public enum Result {
		COMMIT, ABORT, ERROR;
	}

	public EndPoint retrieveServerEndpoint();
	
	public long openTransaction(RemoteClient c);

	public void abortTransaction(long tid);

	public Result closeTransaction(long tid);

}
