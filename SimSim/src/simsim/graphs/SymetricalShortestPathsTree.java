package simsim.graphs;

import java.util.*;

/**
 * @author  Sérgio Duarte (smd@di.fct.unl.pt)
 *
 * Computes a shortest path tree rooted at a given node from a spanner graph.
 * 
 * @param <T> node type.
 */
public class SymetricalShortestPathsTree<T> extends ShortestPathsTree<T> {
    
    SymetricalShortestPathsTree( T root, Set<T> nodes, Set<Link<T>> edges ) {
    	super(root) ;
    	this.edges = new HashSet<Link<T>>( edges ) ;    	

//    	for( Iterator<Link<T>> it = this.edges.iterator() ; it.hasNext() ; ) {
//			Link<T> l = it.next() ;
//			if( l.v.equals(l.w) ) it.remove() ;
//		}

    	dijkstra( new HashSet<T>( nodes ) ) ;
	}
	
    private void dijkstra( Set<T> nodes ) {
    	
    	Map<T, T> parents = new HashMap<T, T>() ;
    	Map<T, Set<Link<T>>> links = new HashMap<T, Set<Link<T>>>() ;    	
    	
    	for( T i : nodes ) {
			costs.put( i, i == root ? 0 : Double.MAX_VALUE ) ;
			hops.put( i, i == root ? 0 : Integer.MAX_VALUE ) ;
    		links.put( i, new HashSet<Link<T>>() ) ;
			children.put( i, new HashSet<T>() ) ;
			parents.put( i, null ) ;
		}
    	
    	// Compute the edges of each node...
    	for( Link<T> l : edges ) {
        	links.get( l.v ).add(l) ;
        	links.get( l.w ).add(l) ;
        }  
    	
		while( ! nodes.isEmpty() ) {
			T min = null ;
			for( T i : nodes )
				min = min == null ? i : costs.get(i) < costs.get(min) ? i : min ;
			
			nodes.remove( min ) ;
			
			for( Link<T> e : links.get( min) ) {
				T child = e.v == min ? e.w : e.v ;
				double alt = costs.get(min) + e.cost ;
				if( alt < costs.get(child) ) {
					costs.put( child, alt) ;
					parents.put( child, min) ;
					hops.put( child, hops.get(min) + 1 ) ;
				}
			}
		}
		
		for( Map.Entry<T, T> i : parents.entrySet() ) {
			T dad = i.getValue() ; T child = i.getKey() ;
			if( dad != null ) {
				children.get( dad ).add( child ) ;
				edges.add( new Link<T>( dad, child, 0) ) ;
			}
		}
	}
}