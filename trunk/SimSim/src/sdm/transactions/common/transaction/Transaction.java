package sdm.transactions.common.transaction;

import simsim.ext.rmi.*;

public interface Transaction extends Remote {
	
	static public enum Result {
		COMMIT, ABORT, ERROR;
	}

	public long openTransaction();

	public void abortTransaction(long tid);

	public Result closeTransaction(long tid);

}
