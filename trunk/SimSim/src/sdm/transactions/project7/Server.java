package sdm.transactions.project7;


import simsim.core.*;
import sdm.transactions.common.*;
import sdm.transactions.common.transaction.*;
import sdm.transactions.project7.msgs.*;

public class Server extends AbstractServer{
	
	public double numberOfCommitedTransactions = 0;
	public double numberOfClosedTransactions = 0;

	public Server() {
		super(64) ;
	}
	
	public void onReboot() {
		super.onReboot();
		initPeriodicTaskCleansing();
	}
	
	protected void initPeriodicTaskCleansing() {
		new PeriodicTask(this,2.0) {
			public void run () {
				((C_TransactionManager)((Server)owner).tm).dropTransactions();
			}	
		};
	}

	public void sendPing(EndPoint src, Message m) {
		udpSend(src,m);
	}
	
	protected AbstractTransactionManager createManager() {
		return new C_TransactionManager( this) ;
	}
	
	
}
