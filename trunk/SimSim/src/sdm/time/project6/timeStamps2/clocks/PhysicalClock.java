package sdm.time.project6.timeStamps2.clocks;


import sdm.time.project6.timeStamps2.*;

public class PhysicalClock extends AbstractClock {
	private Node owner ;
	
	public PhysicalClock( Node n ) {
		owner = n ;
	}
	
	public String name() {
		return "PHY" ;
	}
	
	public TimeStamp value() {
		return new TS( owner.currentTime(), owner.index ) ;
	}
	
	public AbstractClock increment() {
		return this ;
	}
	
	public AbstractClock update( TimeStamp other ) {
		return this ;
	}
	
	
	@SuppressWarnings("serial")
	static class TS implements TimeStamp {
		double value ;
		
		TS( double v, int i ) {
			value = v ;
		}
		
		public int compareTo( TimeStamp other) {
			return compareTo( (TS) other ) ;
		}

		private int compareTo( TS other) {
			return value < other.value ? -1 : 1 ;
		}

		public boolean concurrent(TimeStamp other) {
			return concurrent((TS) other );
		}

		private  boolean concurrent(TS other) {
			return value == other.value ;
		}
		
		public  double value() {
			return value;
		}
		
		public void delay(double incValue) {
			this.value += incValue;
		}

		public String toString() {
			return "" + value ;
		}
	}
}
