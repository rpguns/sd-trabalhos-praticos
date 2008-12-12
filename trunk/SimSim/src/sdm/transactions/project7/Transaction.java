package sdm.transactions.project7;

import simsim.ext.rmi.*;
import simsim.core.*;

public interface Transaction extends Remote {
	
	static public enum Result {
		COMMIT, ABORT, ERROR;
	}

	public EndPoint retrieveServerEndpoint();
	
	public long openTransaction(EndPoint src);

	public void abortTransaction(long tid);

	public Result closeTransaction(long tid);

}
