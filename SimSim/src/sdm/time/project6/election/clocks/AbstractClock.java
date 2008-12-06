package sdm.time.project6.election.clocks;

abstract public class AbstractClock implements Comparable<AbstractClock>{
	
	public String name() {
		return "?" ;
	}

	public int hashCode() {
		return name().hashCode() ;
	}
	
	public abstract TimeStamp value() ;
	
	public abstract AbstractClock increment() ;
	
	public abstract AbstractClock update( TimeStamp other ) ;

	public int compareTo( AbstractClock other) {
		return name().compareTo(other.name() ) ; 
	}
}
