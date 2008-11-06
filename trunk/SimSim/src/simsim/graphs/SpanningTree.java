package simsim.graphs;

import java.util.* ;

public class SpanningTree<T> {
    
    protected SpanningTree() {
        this( new ArrayList<T>(), new ArrayList<Link<T>>() ) ;
    }
    
    public SpanningTree( Collection<T> nodes, Collection<Link<T>> links) {
        this.nodes = nodes ;
        this.links = links ;
    }
       
                
    @SuppressWarnings("unchecked")
	public boolean equals( Object other ) {
        return other != null && equals( (SpanningTree<T>) other ) ;
    }
    
    public boolean equals( SpanningTree<T> other ) {
        if( nodes.size() != other.nodes.size() || links.size() != other.links.size() ) return false ;
        if( nodes.containsAll( other.nodes ) ) return false ;
        if( links.containsAll( other.links ) ) return false ;       
        return true ;
    }
    
    public Collection<T> nodes() {
    	return nodes ;
    }
    
    public Collection<Link<T>> links() {
    	return links ;
    }
    
    Collection<T> nodes ;
    Collection<Link<T>> links ;
}
