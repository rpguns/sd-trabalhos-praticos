package simsim.graphs;

public class Link<T> implements Comparable<Link<T>> {
    public static int INFINITE = 100000 ;
    
    public Link( T v, T w, double cost) {
        this.v = v ;
        this.w = w ;
        this.cost = cost ;
        if( v.equals(w) ) Thread.dumpStack() ;
    }
    
    @SuppressWarnings("unchecked")
	public boolean equals(final Object other ) {
    	return other != null && equals( (Link<T>) other) ;
    }

    public boolean equals(final Link<T> other ) {
        return (v.equals(other.v) && w.equals(other.w)) || (v.equals(other.w) && w.equals(other.v)) ;
    }
    
    public int hashCode() {
        return v.hashCode() ^ w.hashCode() ;
    }
    
    public String toString() {
        return v + "<->" + w + ": " + cost ;
    }
    
    public int compareTo(Link<T> other) {
    	if( this.cost == other.cost ) {
    		int i = v.hashCode() - other.hashCode() ;
            return i == 0 ? ( w.hashCode() - other.w.hashCode() ) : i ;    		
    	}
    	else return (this.cost < other.cost ? -1 : 1) ;
    }
    
    public T v, w ;
    public double cost ;
}