package sdm.transactions.project7;


import simsim.core.*;import java.util.*;
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
		initEthnicCleansing();
	}
	
	protected void initEthnicCleansing() {
		new Task(200) {
			public void run() {
				C_TransactionManager manager = (C_TransactionManager)tm;
				if (!manager.transactionClients.isEmpty()) {
					Long i = manager.transactionClients.keySet().iterator().next();
					RemoteClient c = manager.transactionClients.get(i);
					System.err.println("Crashed TID: "+i);
					
					c.crashThyOwner();
					System.err.println("CRASHZORS HAPPENEDZORS!");
					System.err.flush();
				}
				reSchedule( 10 + 5000*new Random().nextDouble() ) ;
			}
		} ;
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
