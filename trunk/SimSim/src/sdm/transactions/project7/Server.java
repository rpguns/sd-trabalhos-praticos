package sdm.transactions.project7;



import sdm.transactions.common.*;
import sdm.transactions.common.transaction.*;

public class Server extends AbstractServer {
	
	public double numberOfCommitedTransactions = 0;
	public double numberOfClosedTransactions = 0;

	public Server() {
		super(64) ;
	}

	protected AbstractTransactionManager createManager() {
		return new C_TransactionManager( this) ;
	}
	
}
