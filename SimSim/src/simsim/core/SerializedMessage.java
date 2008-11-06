package simsim.core;

import java.awt.Color;
import java.io.* ;

/**
 * An alternative class for creating messages.
 * Messages derived from this class allow each node to receive a private copy of any message received.
 * These messages can be safely modified without any effects being immediately visible to other nodes, which
 * is not the case for messages derived from Message.
 * 
 * @author  Sérgio Duarte (smd@di.fct.unl.pt)
 *
 */
@SuppressWarnings("serial")
abstract public class SerializedMessage extends Message implements Serializable {
	
	protected SerializedMessage() {
		this( false, Color.gray ) ;
	}
	
	protected SerializedMessage( boolean visible, Color color ) {
		this.color = color ;
		this.visible = visible ;
	}
	
	public EncodedMessage encode() {
		return new MarshalledMessage( this ) ;
	}
	
}

class MarshalledMessage implements EncodedMessage {
	
	private byte[] data ;

	public int length() {
		return data != null ? data.length : -1 ;
	}
	
	protected MarshalledMessage( SerializedMessage m ) {
		data = encode( m ) ;
	}		
		
		
	private byte[] encode( SerializedMessage m ) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream() ;
			ObjectOutputStream oos = new ObjectOutputStream( baos ) ;
			oos.writeObject( m ) ;
			oos.close() ;
			baos.close() ;
			return baos.toByteArray() ;
		} catch (IOException e) {
			e.printStackTrace() ;
			return null ;
		}
	}
	
	public Message decode() {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream( data ) ;
			ObjectInputStream ois = new ObjectInputStream( bais ) ;
			Message res = (Message) ois.readObject() ;
			ois.close();
			bais.close() ;
			return res ;
		} catch (Exception e) {
			e.printStackTrace();
			return null ;
		}
	}	
}
