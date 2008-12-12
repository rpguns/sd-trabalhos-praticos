package sdm.transactions.project7;

import java.util.*;

import sdm.transactions.common.grid.Grid;
import sdm.transactions.common.grid.GridOperations;
import sdm.transactions.common.time.*;
import sdm.transactions.common.transaction.*;


public class C_TentativeGridTransaction extends AbstractTransaction implements GridOperations {

	Grid committed; // a reference to the committed version of the grid...
	Grid tentative; // the tentative version of the grid associated with this transaction...
	boolean cUpdated[][], sUpdated[][]; // tells which grid cells were written to

	public C_TentativeGridTransaction(long tid, TimeStamp begin, Grid commited) {
		super(tid, begin);
		this.committed = commited;
		this.tentative = commited.makeCopy();
		this.cUpdated = new boolean[committed.rows()][commited.cols()];
		this.sUpdated = new boolean[committed.rows()][commited.cols()];
		for (int i = 0; i < commited.rows(); i++)
			for (int j = 0; j < commited.cols(); j++)
				cUpdated[i][j] = sUpdated[i][j] = false;
	}

	// If the transaction is committed then written values are copied back to
	// the committed version...
	public void commitChanges() {
		for (int i = 0; i < tentative.rows(); i++)
			for (int j = 0; j < tentative.cols(); j++) {
				if (cUpdated[i][j])
					committed.writeColor(tid, i, j, tentative.readColor(tid, i, j));
				if (sUpdated[i][j])
					committed.writeShape(tid, i, j, tentative.readShape(tid, i, j));
			}
	}

	public boolean updated(int i, int j) {
		return cUpdated[i][j] || sUpdated[i][j];
	}

	public int readColor(long tid, int i, int j) {
		readSet.add("C<" + i + " " + j+">");
		return tentative.readColor(tid, i, j);
	}

	public int readShape(long tid, int i, int j) {
		readSet.add("S<" + i + " " + j +">");
		return tentative.readShape(tid, i, j);
	}

	public void writeColor(long tid, int i, int j, int v) {
		cUpdated[i][j] = true;
		tentative.writeColor(tid, i, j, v);
		writeSet.add("C<" + i + " " + j + ">");
	}

	public void writeShape(long tid, int i, int j, int v) {
		sUpdated[i][j] = true;
		tentative.writeShape(tid, i, j, v);
		writeSet.add("S<" + i + " " + j + ">");
	}

	public int[] gridSize(long tid) {
		return committed.gridSize(tid);
	}

	public boolean hasConflictWith(Set<String> otherReadSet, Set<String> otherWriteSet) {

		//Write-type conflicts
		for (String x:this.writeSet) {
			for (String y:otherWriteSet)
				if (x.equals(y))
					return true;
		}
/*
		for (String x:readSet) {
			for (String y:secondWriteSet)
				if (x.subSequence(arg0, arg1)y) == 0)
				return true;
		}
*/

		return false;
	}

	protected Set<String> readSet = new HashSet<String>();
	protected Set<String> writeSet = new HashSet<String>();
}
