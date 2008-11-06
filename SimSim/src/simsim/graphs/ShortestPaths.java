package simsim.graphs;

import java.util.*;

/**
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 * 
 * Computes a shortest path tree rooted at a given node from a spanner graph.
 * 
 * @param <T>
 *            node type.
 */
public class ShortestPaths<T> {

	private Set<T> nodes ;
	private Map< Link<T>, Set<Link<T>>> paths ;
	private Map< T, ShortestPathsTree<T>>  trees ;

	public ShortestPaths( kSpanner<T> spanner) {
		
		nodes = new HashSet<T>( spanner.nodes() ) ;
		paths = new HashMap<Link<T>, Set<Link<T>>>() ;
		trees = new HashMap<T, ShortestPathsTree<T>>() ;
		
		for( T i : nodes) {
			int ii = spanner.indexOf(i) ;
			ShortestPathsTree<T> t = new ShortestPathsTree<T>( i, spanner ) ;
			for( T j : spanner.nodes() ) {
				int jj = spanner.indexOf(j) ;
				if( jj < ii ) {
					Link<T> ij = new Link<T>(i, j, t.cost(j) ) ;
					paths.put( ij, t.path(j)) ;
				}
			}
		}
	}
	
	public ShortestPathsTree<T> symetricTree( T root ) {
		ShortestPathsTree<T> res = trees.get( root ) ;
		if( res == null ) {
			Set<Link<T>> edges = new HashSet<Link<T>>() ;
			for( T i : nodes )
				if( ! i.equals(root) )
					edges.addAll( paths.get( new Link<T>(root,i,0) ) ) ;
						
			res = new SymetricalShortestPathsTree<T>( root, nodes, edges ) ;
		}
		return res ;
	}
}
