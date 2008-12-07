package sdm.transactions.common.time;

import simsim.core.*;

public class PhysicalClock extends Clock {
	private AbstractNode owner ;
	
	public PhysicalClock( AbstractNode owner ) {
		this.owner = owner ;
	}
	
	public TimeStamp value() {
		return new PhysicalClockTimeStamp( owner.currentTime() ) ;
	}
	
	public Clock increment() {
		return this ;
	}
	
	public Clock update( TimeStamp other ) {
		return this ;
	}
	
	public String name() {
		return "(<)" ;
	}
	
	static class PhysicalClockTimeStamp implements TimeStamp {
		final double value ;
		
		PhysicalClockTimeStamp( double v ) {
			value = v ;
		}
		
		public int compareTo( TimeStamp other) {
			return compareTo( (PhysicalClockTimeStamp) other ) ;
		}

		private int compareTo( PhysicalClockTimeStamp other) {
			return value < other.value ? -1 : 1 ;
		}

		public boolean concurrent(TimeStamp other) {
			return concurrent((PhysicalClockTimeStamp) other );
		}

		private  boolean concurrent(PhysicalClockTimeStamp other) {
			return value == other.value ;
		}

		public String toString() {
			return "" + value ;
		}
	}
}
