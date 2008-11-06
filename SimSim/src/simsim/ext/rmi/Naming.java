package simsim.ext.rmi;

import java.util.* ;

import simsim.core.*;

/**
 * The class in the SimSim RMI/RPC package that plays the part of the Java RMI registry. 
 * It offers a simplified rmiregistry-like functionality, allowing a remote object to register itself with a name and clients to perform lookups.
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 *
 */
public class Naming {

	@SuppressWarnings("unchecked")
	public static <T> T lookupServer( EndPoint client, String url ) {
		return (T) registry.get( url ).clientProxy( client ) ;
	}
	
	public static void rebindServer( String url, UnicastRemoteObject stub ) {
		registry.put( url, stub ) ;
	}
	
	private static Map<String, UnicastRemoteObject> registry = new HashMap<String, UnicastRemoteObject>() ;
}

