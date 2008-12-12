package sdm.transactions.project7;

import simsim.ext.rmi.*;
import java.util.*;

public class ClientMiddleware extends UnicastRemoteObject {

	public HashSet<Long> activeTransactions;
	
	public ClientMiddleware(Client owner) {
		super(owner.address);
		activeTransactions = new HashSet<Long>(100);
	}
	
	public void removeTransaction(long tid) {
		activeTransactions.remove(tid);
	}
	
	public void addTransaction(long tid) {
		activeTransactions.add(tid);
	}
	
	public boolean isActive(long tid) {
		return activeTransactions.contains(tid);
	}
	
}
