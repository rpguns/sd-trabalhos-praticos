package sdm.transactions.common.time;

abstract public class Clock {
	
	public String name() {
		return "AbstractClock" ;
	}

	public int hashCode() {
		return name().hashCode() ;
	}
	
	public abstract TimeStamp value() ;
	
	public abstract Clock increment() ;
	
	public abstract Clock update( TimeStamp other ) ;
}
