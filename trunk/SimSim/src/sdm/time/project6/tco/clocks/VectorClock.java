package sdm.time.project6.tco.clocks;

public class VectorClock extends AbstractClock {
	final int index ;
	final int[] values ;
	
	public VectorClock( int ownerIndex, int length ) {
		index = ownerIndex ;
		values = new int[ length ] ;
		for( int i = 0 ; i < values.length ; i++ )
			values[i] = 0 ;
	}
	
	public String name() {
		return "VEC" ;
	}
	
	public TimeStamp value() {
		return new TS( values ) ;
	}
	
	public AbstractClock increment() {
		values[index]++ ;	
		return this ;
	}
	
	public AbstractClock update( TimeStamp other ) {
		TS ots = (TS) other ;
		for( int i = 0 ; i < values.length ; i++ )
			values[i] = Math.max( values[i], ots.values[i]) ;
		return this ;
	}
	
	@SuppressWarnings("serial")
	static class TS implements TimeStamp {
		final int[] values ;
		
		TS( int[] va ) {
			values = new int[ va.length ] ;
			System.arraycopy(va, 0, values, 0, va.length) ;
		}
		
		public String toString() {
			String res = "<";
			for (int i : values)
				res += " " + i + " ";
			return res + ">";
		}
		
		public int compareTo( TimeStamp other) {
			return compareTo( (TS) other ) ;
		}

		public boolean equals( Object other ) {
			return other != null && equals( (TS) other ) ;
		}
		
		private int compareTo( TS other ) {
			return equals( other) ? 0 : lessOrEqual( other ) ? -1 :  1 ;
		}
		
		private boolean lessOrEqual( TS other ) {
			for( int i = 0 ; i < values.length ; i++ )
				if( values[i] > other.values[i] ) return false ;					
			return true ;
		}
		
		private boolean equals( TS other ) {
			for( int i = 0 ; i < values.length ; i++ )
				if( values[i] != other.values[i] )	return false ;					
			return true ;
		}

		public boolean concurrent(TimeStamp other) {
			return other != null & concurrent( (TS) other ) ;
		}

		private boolean concurrent( TS other) {
			return ! equals( other) && ! this.lessOrEqual( other ) && ! other.lessOrEqual( this ) ;
		}
	}
}