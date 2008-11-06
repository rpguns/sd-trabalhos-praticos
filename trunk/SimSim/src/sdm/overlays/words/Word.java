package sdm.overlays.words;

import java.math.* ;
import java.security.*;

public class Word implements Comparable<Word> {
	public String value ;
	public long hashValue ;
		
	Word( String w ) {
		this.value = w ;
		this.hashValue = hashValue(w) ;
		assert hashValue >= 0 && hashValue <= Long.MAX_VALUE ;
	}

	public long lHashValue() {
		return hashValue ;
	}
	
	public double dHashValue() {
		return (double)hashValue / Long.MAX_VALUE ;
	}
	
	public boolean equals( Object other ) {
		return other != null && equals( (Word)other ) ;
	}

	public boolean equals( Word other ) {
		return value.equals( other.value ) ;
	}

	public int hashCode() {
		return (int)((hashValue >>> 32) ^ ( hashValue & 0xFFFFFFFFL)) ;
	}
	
	
	public int compareTo( Word other ) {
		return this.value.compareTo( other.value ) ;
	}
	
	//-------------------------------------------------------------------------------------------------
	private long hashValue( String s ) {
		digest.reset() ;
		digest.update( s.getBytes() );
		return new BigInteger( digest.digest() ).abs().mod( maxHashValue ).longValue() ;
	}
	
	private static MessageDigest digest ;
	private static final BigInteger maxHashValue ;
	
	static {
		maxHashValue = new BigInteger( "" + Long.MAX_VALUE ) ;
		try {
			digest = java.security.MessageDigest.getInstance("MD5");
		} catch (Exception x) {
			digest = null ;
			x.printStackTrace();
		}
	}
}
