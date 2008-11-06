package simsim.graphs;

import java.util.* ;

/**
 *
 * @author  smd
 */
public class kSpanner<T> extends Graph<T> {
   
	public float K ;
    
    private int n ;
    private double[][] cT ;
    private boolean[] addedNodes ;
    
    public kSpanner( float k, Collection<T> nc, Collection<Link<T>> lc ) {
        K = k ;
        
        initCostTable( nc ) ;
        
    	for( Link<T> l : new TreeSet<Link<T>>(lc) )
    		insertLink( k, l ) ;
        
        for( Link<T> l : E ) {
        	L.get( l.v ).add(l) ;
        	L.get( l.w ).add(l) ;
        }        	
    }
        
    private void initCostTable( Collection<T> nc ) {
        
    	addedNodes = new boolean[ nc.size() ] ;
   	
        for( T i : nc ) {
        	N.add( i ) ;
        	L.put( i, new ArrayList<Link<T>>() ) ;
        }
        	
         
        n = N.size() ;
        cT = new double[n][n] ;
        
        for( int i = 0 ; i < n ; i++ )
            for( int j = 0 ; j <= i ; j++ )
                cT[i][j] = cT[j][i] = Float.MAX_VALUE / 5;
    }
    
    
    private void insertLink( float K, Link<T> l ) {
        int v = indexOf( l.v ) ;
        int w = indexOf( l.w ) ;
        
        if( cT[v][w] <= K * l.cost ) return ;
        
        addedNodes[v] = addedNodes[w] = true ;
        
        E.add( l ) ;
        
        cT[v][w] = cT[w][v] = l.cost ;
        
        for( int k = 0 ; k < n ; k++ )
        	for( int i = 0 ; i < n ; i++ )
        		if( addedNodes[i] )
        		for( int j = 0 ; j < n ; j++ ) 
        			if( addedNodes[j] ) {
                         cT[i][j] = cT[j][i] = Math.min( cT[i][j], cT[i][k] + cT[k][j] ) ;
        			}
    }
}
