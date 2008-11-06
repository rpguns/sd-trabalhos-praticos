package simsim.core;

/**
 * This interface is used to convert back a network message in some encoded format to a message object.
 * 
 * @author  Sérgio Duarte (smd@di.fct.unl.pt)
 *
 */
public interface EncodedMessage {
	
	/**
	 * Tells the size of the encoded message in bytes.
	 * @return the size of the encoded message in bytes.
	 */
	public int length() ;
	
	/**
	 * Decodes the implementing object into a message object.
	 * @return The encoded message.
	 */
	public Message decode() ;
	
}
