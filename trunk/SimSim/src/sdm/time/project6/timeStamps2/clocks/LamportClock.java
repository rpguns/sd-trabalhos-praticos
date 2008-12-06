package sdm.time.project6.timeStamps2.clocks;

public class LamportClock extends AbstractClock {
	
	int value ;
	int index ;
	
	public LamportClock( int index ) {
		this.value = 0 ;
		this.index = index ;
	}
	
	public String name() {
		return "LAM" ;
	}
	
	public TimeStamp value() {
		return new TS( value, index ) ;
	}
	
	public AbstractClock increment() {
		value += 1 ;
		return this ;
	}
	
	public AbstractClock update( TimeStamp other ) {
		value = Math.max(value, ((TS)other).value) ;
		return this ;
	}
	
	
	@SuppressWarnings("serial")
	static class TS implements TimeStamp {
		final int value ;
		final int index ;

		TS( int value, int index ) {
			this.value = value ;
			this.index = index ;
		}
		
		public int compareTo( TimeStamp other) {
			return compareTo( (TS) other ) ;
		}

		private int compareTo( TS other) {			
			return value == other.value ? (index < other.index ? -1 : 1) : (value < other.value ? -1 : 1) ;
		}

		public boolean concurrent(TimeStamp other) {
			return concurrent((TS) other );
		}

		private  boolean concurrent(TS other) {
			return false;
		}

		//TO FUCKING DO OR DELETE OR ENRABATE
		public  double value() {
			return 0;
		}
		
		public void delay(double incValue) {
			//thumbleweed rolls by
		}
		
		public String toString() {
			return "" + value ;
		}
	}
}