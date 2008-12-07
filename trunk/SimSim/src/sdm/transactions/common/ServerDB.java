package sdm.transactions.common;

import java.util.*;

import simsim.utils.*;

public class ServerDB {
	
	
	public static int store( AbstractServer n) {
		servers.add( n) ;
		k2s.put( n.key, n ) ;
		return n.key ;
	}

	public Set<Integer> keys() {
		return k2s.keySet();
	}

	public static void dispose( AbstractServer n) {
		if (n != null) {
			if( k2s.remove( n.key) != null ) {
				servers.remove( n ) ;
				NodeDB.dispose(n) ;
				n.dispose();			
			}
		}
	}

	public static int size() {
		return k2s.size();
	}

	public static AbstractServer randomServer() {
		return servers.randomElement();
	}

	private static RandomList<AbstractServer> servers = new RandomList<AbstractServer>();
	private static TreeMap<Integer, AbstractServer> k2s = new TreeMap<Integer, AbstractServer>();

}