package simsim.graphs;

import java.util.* ;

/**
 *
 * @author  smd
 */
public class Graph<T> {
   
    protected List<T> N = new ArrayList<T>() ;
    protected SortedSet<Link<T>> E = new TreeSet<Link<T>>() ;
    protected Hashtable<T, ArrayList<Link<T>>> L = new Hashtable<T, ArrayList<Link<T>>>() ;
    
    protected Graph() {    	
    }
    
    public Graph(Collection<T> nc, Collection<Link<T>> lc ) {
    	N.addAll( nc ) ;
    	E.addAll( lc ) ;
    	
    	for( T i : nc )
    		L.put( i, new ArrayList<Link<T>>() ) ;
    		
        for( Link<T> l : E ) {
        	L.get( l.v ).add(l) ;
        	L.get( l.w ).add(l) ;
        }        	
        
    }
        
    public int numberOfNodes() {
    	return N.size() ;
    }
         
    private Hashtable<T, Integer> node2index = new Hashtable<T, Integer>() ;
    
    int indexOf( T node ) {
    	Integer i = node2index.get( node ) ;
        if( i == null ) {
        	i = N.indexOf( node ) ;
        	node2index.put( node, i ) ;
        }
        return i ;
    }
    
    
    public Collection<T> neighbours( T node ) {
        try {
            HashSet<T> n = new HashSet<T>() ;
            for( Link<T> l : L.get( node ) ) {
                n.add( l.v ) ;
                n.add( l.w ) ;
            }
            return n ;
        } catch( Exception x ) {
            return new ArrayList<T>() ;
        }
    }
    
    public Set<Link<T>> edges() {
    	return E ;
    }
    
    public List<Link<T>> edges( T v) {
    	return L.get(v) ;
    }
    
    public Collection<T> nodes() {
    	return N ;
    }
    
    public Collection<Link<T>> links() {
    	return E ;
    }
}
