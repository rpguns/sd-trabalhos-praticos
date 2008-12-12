package sdm.transactions.project7;

import simsim.ext.rmi.*;
import java.util.*;

public class ClientMiddleware extends UnicastRemoteObject implements RemoteClient {

	public HashSet<Long> activeTransactions;
	public Client owner;
	
	public ClientMiddleware(Client owner) {
		super(owner.address);
		this.owner = owner;
		activeTransactions = new HashSet<Long>(100);
	}
	
	public void removeTransaction(long tid) {
		activeTransactions.remove(tid);
	}
	
	public void clear() {
		activeTransactions = new HashSet<Long>(100);
	}
	
	public void addTransaction(long tid) {
		activeTransactions.add(tid);
	}
	
	public void crashThyOwner() {
		owner.putOffline();
		this.clear();
		System.err.println("Turned off the node "+owner.address);
	}
	
	/* (non-Javadoc)
	 * @see sdm.transactions.project7.RemoteClient#isActive(long)
	 */
	public boolean isActive(long tid) {
		return activeTransactions.contains(tid);
	}
	
}
