package simsim.core;

import java.util.*;

/**
 * The abstract base class from which network types are derived.
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 * 
 */
abstract public class Network implements Displayable {

	final double JITTER = Globals.get("Net_Jitter", 0.2 ) ;

	/**
	 * By setting the seed of the internal random generator, network address creation will follow a repeatable pattern.
	 * @param seed value of the seed to be used to establish the random sequence. Use 0 for random addresses.
	 */
	abstract public void setRandomSeed( long seed ) ;

	abstract public Network init() ;
	
	/**
	 * Returns a set of the NetAddresss that lists all the nodes presently in the network.
	 * 
	 * @return the set of the NetAddresss of the nodes in the network.
	 */
	abstract public Set<NetAddress> addresses();

	/**
	 * Creates a new node address and a default endpoint that will be associated with the given message handler.
	 * @param handler - the default handler for all the messages sent to this address or endpoint.
	 * @return The newly created address.
	 */
	abstract public NetAddress createAddress( MessageHandler handler);

	/**
	 * Request the network implementation to replace an existing address with a fresh one.
	 * Intended to allow nodes to go offline and then return later with a new address.
	 * @param other - the address being replaced.
	 * @return The replacement address.
	 */
	abstract public NetAddress replaceAddress( NetAddress other );

}
