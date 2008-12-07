package sdm.transactions.common.time;

public interface TimeStamp extends Comparable<TimeStamp> {
	
	boolean concurrent( TimeStamp other ) ;
}