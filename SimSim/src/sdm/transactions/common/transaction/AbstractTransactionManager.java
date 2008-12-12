package sdm.transactions.common.transaction;
import sdm.transactions.project7.*;

import java.util.*;

import simsim.core.*;
import simsim.ext.rmi.*;

import sdm.transactions.common.*;
import sdm.transactions.common.time.*;
import sdm.transactions.common.persistence.*;


abstract public class AbstractTransactionManager extends UnicastRemoteObject implements Transaction, Displayable {

	protected Clock clock;
	protected AbstractServer owner;

	protected AbstractTransactionManager(AbstractServer owner, Clock clock) {
		super(owner.address);
		this.owner = owner;
		this.clock = clock;
		this.tidCounter = SafeStorage.load(owner, "TID", (long) owner.sid << 32);
	}

	public EndPoint retrieveServerEndpoint() {
		throw new RuntimeException("Not implemented...");
	}
	
	protected abstract AbstractTransaction createTransaction(long tid, TimeStamp timeStamp);

	public long openTransaction(RemoteClient c) {
		throw new RuntimeException("Not implemented...");
	}

	public void abortTransaction(long tid) {
		throw new RuntimeException("Not implemented...");
	}

	public Result closeTransaction(long tid) {
		throw new RuntimeException("Not implemented...");
	}

	@SuppressWarnings("unchecked")
	protected <T> T get(long tid) {
		AbstractTransaction res = transactions.get(tid);
		if (res == null) {
			if (ownTid(tid)) {
				System.out.println(tid);
				throw new RuntimeException("Unknown transaction tid...");
			}
			res = this.createTransaction(tid, clock.increment().value());
			transactions.put(tid, res);
		}
		return (T) res;
	}

	@SuppressWarnings("unchecked")
	protected <T> T remove(long tid) {
		AbstractTransaction res = transactions.remove(tid);
		return (T) res;
	}

	protected boolean ownTid(long tid) {
		return tid >> 32 == owner.sid;
	}

	@SuppressWarnings("unchecked")
	public <T> SortedSet<T> transactions() {
		SortedSet<T> res = new TreeSet<T>();
		for (AbstractTransaction i : transactions.values())
			res.add((T) i);
		return res;
	}

	protected void saveTID() {
		SafeStorage.save(owner, "TID", tidCounter);
	}

	// transaction counter of the form <serverid, counter> encoded in a long.
	protected long tidCounter;
	// keeps current uncommitted transactions
	protected Map<Long, AbstractTransaction> transactions = new HashMap<Long, AbstractTransaction>();
}
