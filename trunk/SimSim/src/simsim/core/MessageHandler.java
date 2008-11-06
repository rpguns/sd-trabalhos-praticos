package simsim.core;

/**
 * This interface is used whenever an incoming message arrives at a node.
 * This interface can be extended in order to have the implementing object process specific message types.
 * @author  Sérgio Duarte (smd@di.fct.unl.pt)
 *
 */
public interface MessageHandler {

	/**
	 * Called when sending a message fails due to the destination endpoint being offline.
	 * @param dst The endpoint that was targeted.
	 * @param m The message that failed to reach the targeted node/endpoint.
	 */
	public void onSendFailure( EndPoint dst, Message m ) ;
	
	/**
	 * Called to process an incoming asynchronous message.
	 * @param src The source endpoint of the message.
	 * @param m The incoming message.
	 */
	public void onReceive( EndPoint src, Message m ) ;
	
	/**
	 * Called to process an incoming blocking message.
	 * @param chn The channel connection that brought the message and identifies its origin.
	 * @param m The incoming message.
	 */
	public void onReceive( TcpChannel chn, Message m ) ;
}
