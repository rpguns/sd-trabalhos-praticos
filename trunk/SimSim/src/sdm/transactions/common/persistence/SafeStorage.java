package sdm.transactions.common.persistence;

import java.io.* ;
import java.util.* ;

import sdm.transactions.common.*;


public class SafeStorage {

	static public void save( Node n, String info_key, Serializable data ) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream() ;
			ObjectOutputStream oos = new ObjectOutputStream( baos ) ;
			oos.writeObject( data ) ;
			oos.flush() ;
			baos.close() ;
			persistentStorage.put( n.key + "-" + info_key, baos.toByteArray() ) ;
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	static public <T> T load( Node n, String info_key, T defaultValue ) {
		if( persistentStorage.containsKey( n.key + "-" + info_key ) )
			return load( n, info_key ) ;
		else return defaultValue ;
	}
	
	@SuppressWarnings("unchecked")
	static public <T> T load( Node n, String key ) {
		byte[] data = persistentStorage.get( n.key + "-" + key ) ;
		
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream( data ) ;
			ObjectInputStream ois = new ObjectInputStream( bais ) ;		
			T res = (T) ois.readObject() ;
			ois.close() ;
			bais.close() ;
			return res ;
		} catch (Exception x) {
			x.printStackTrace() ;
			throw new RuntimeException("No valid data in persistent storage for: " + key + " at node: " + n.key ) ;
		}
	}

	static Map<String, byte[]> persistentStorage = new HashMap<String, byte[]>() ;
}
