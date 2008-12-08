package sdm.transactions.centralizedBasePackage;



import sdm.transactions.common.*;
import sdm.transactions.common.transaction.*;

public class Server extends AbstractServer {

	public Server() {
		super(64) ;
	}

	protected AbstractTransactionManager createManager() {
		return new C_TransactionManager( this) ;
	}
	
}
