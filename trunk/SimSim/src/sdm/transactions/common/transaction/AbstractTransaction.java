package sdm.transactions.common.transaction;

import sdm.transactions.common.time.*;

abstract public class AbstractTransaction implements Comparable<AbstractTransaction> {

	protected long tid ;
	protected TimeStamp timeStamp ;

	protected AbstractTransaction() {}		

	protected AbstractTransaction( long tid, TimeStamp timeStamp ) {		
		this.tid = tid ;
		this.timeStamp = timeStamp ;
	}
	
	static AbstractTransaction createTransaction( long tid, TimeStamp stamp, Object commited ) {
		throw new RuntimeException("Must override this method") ;
	}

	public long tid() {
		return tid ;
	}
	
	public int hashCode() {
		return new Long( tid ).hashCode() ;
	}
	
	public int compareTo( AbstractTransaction other) {
		return timeStamp.compareTo( other.timeStamp ) ;
	}

	public boolean equals( Object other ) {
		return other != null & equals( (AbstractTransaction) other ) ;
	}
	
	private boolean equals( AbstractTransaction other ) {
		return tid == other.tid;
	}
	
	public String toString() {
		return Long.toHexString(tid) + ":" + timeStamp.toString() ;
	}
}
