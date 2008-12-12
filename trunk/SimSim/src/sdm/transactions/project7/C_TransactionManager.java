package sdm.transactions.project7;

import java.awt.Graphics2D;
import java.util.*;

import simsim.ext.rmi.*;
import sdm.transactions.common.*;
import sdm.transactions.common.grid.*;
import sdm.transactions.common.persistence.*;
import sdm.transactions.common.time.*;
import sdm.transactions.common.transaction.*;
import static sdm.transactions.common.transaction.Transaction.Result.*;

public class C_TransactionManager extends AbstractTransactionManager implements TransactionalGridOperations {
	
	protected HashMap< Long,List< Pair<Set<String>,Set<String> > > > transactionCollisionSets = 
		new HashMap< Long,List< Pair< Set<String>,Set<String> > > >(100);
	
	public C_TransactionManager( AbstractServer owner ) {
		super( owner, new PhysicalClock( owner ) ) ;
		Naming.rebindServer("//Server" + owner.sid + "/tgo", this ) ; 		
	}

	@Override
	protected AbstractTransaction createTransaction(long tid, TimeStamp timeStamp) {
		return new C_TentativeGridTransaction( tid, timeStamp, owner.grid );
	}

	public long openTransaction() {
		C_TentativeGridTransaction t = new C_TentativeGridTransaction( tidCounter++, clock.increment().value(), owner.grid ) ;
		transactions.put( t.tid(), t ) ;
		
		//Adquire uma nova cache de transaccoes commited entre tempo de chegada e tempo de saída
		transactionCollisionSets.put(t.tid(), new LinkedList<Pair<Set<String>,Set<String>>>());
		System.err.println("BEGIN:" + t.tid() ) ;
		return t.tid() ;
	} 
	
	protected void updateColisions(C_TentativeGridTransaction commitedTransaction) {
		
		Pair<Set<String>,Set<String>> possibleConflicts = 
			new Pair<Set<String>,Set<String>>(commitedTransaction.readSet,commitedTransaction.writeSet);
		
		for (AbstractTransaction t : transactions.values()) {
			C_TentativeGridTransaction taux = (C_TentativeGridTransaction) t;
			List<Pair<Set<String>,Set<String>>> collisionSet = transactionCollisionSets.get(taux.tid());
			collisionSet.add(possibleConflicts);
		}
		
	}
	protected boolean collisionsExistWith(C_TentativeGridTransaction t) {
		
		//Retrieves the collision list for the given transaction
		List<Pair<Set<String>,Set<String>>> tCollisionSet = 
			transactionCollisionSets.get(t.tid());
		
		//Iterates over the previously committed transactions and checks for conflicts
		for (Pair<Set<String>,Set<String>> currentPair : tCollisionSet) {
			if (t.hasConflictWith(currentPair.getFirst(), currentPair.getFirst())) 
				return true;
		}
		
		return false;
	}
	
	// ERROR indicates that we are attempting to close a transaction
	// that this server does not know about...
	public Result closeTransaction( long tid) {
		C_TentativeGridTransaction t = super.remove( tid ) ;
		if( t != null ) {
			//Result res = transactions().isEmpty() ? COMMIT : ABORT ;
			
			Result res = COMMIT;
				
			SortedSet<C_TentativeGridTransaction> currentTrans = this.transactions();
			
			if (collisionsExistWith(t))
				res = ABORT;
			
			System.out.println("Total Concurrent Transactions: " + transactions() ) ;

			if( res == COMMIT ) { 
				t.commitChanges() ; 
				updateColisions(t) ;
				
				//Estatística
				((Server)owner).numberOfCommitedTransactions++; 
				}

			super.saveTID() ;
			SafeStorage.save( owner, "grid", owner.grid ) ;
			((Server)owner).numberOfClosedTransactions++;
			return res ;
		}
		else return ERROR ;
	}
	
	private C_TentativeGridTransaction tentative( long tid ) {
		return super.get(tid) ;
	}
	
	public int[] gridSize(long tid) {		
		return tentative( tid ).gridSize(tid) ;
	}

	public int readColor(long tid, int i, int j) {
		return tentative( tid ).readColor(tid, i, j) ;
	}

	public int readShape(long tid, int i, int j) {
		return tentative( tid ).readShape(tid, i, j) ;
	}

	public void writeColor(long tid, int i, int j, int v) {
		tentative( tid).writeColor(tid, i, j, v) ;
	}

	public void writeShape(long tid, int i, int j, int v) {
		tentative( tid).writeShape(tid, i, j, v) ;
	}
	
	public void display(Graphics2D gu, Graphics2D gs) {

		int rows = owner.grid.rows();
		int cols = owner.grid.cols();
	
		Set<C_TentativeGridTransaction> ts = transactions();
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++) {
				int k = 0;
				for (C_TentativeGridTransaction t : ts)
					if (t.updated(i, j))
						k++;

				//owner.grid.display(i, j, gs, k, owner.isOffline());
				
			}
	}
}