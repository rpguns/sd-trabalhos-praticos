package simsim.core;

import java.io.*;
import java.util.*;

/**
 * This class manages global simulation properties.
 * 
 * Global properties must be set/initialized/loaded at startup (main method).
 * 
 * Global properties are identified by a key, usually a string, whose format points to the module affected.
 * 
 * @author  Sérgio Duarte (smd@di.fct.unl.pt)
 *
 */
public class Globals {

	/**
	 * Obtains the value of a property. 
	 * @param <T> The type of the property value.
	 * @param key  The key of the property.
	 * @return The value of the property cast to its type, or null if the property has not been set.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T get(Object key) {
		return (T) globals.get(key);
	}

	/**
	 * Obtains the value of a property. 
	 * @param <T> The type of the property value.
	 * @param key The key of the property.
	 * @param defaultValue The default value of the property to use if the property has not been set.
	 * @return The value of the property cast to its type, or the default value if the property has not been set.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T get(Object key, T defaultValue) {
		Object res = globals.containsKey(key) ? globals.get(key) : defaultValue ;
		return (T) res ;
	}

	/**
	 * Sets/replaces the value of a property.
	 * @param key The key of the property.
	 * @param value The new value of the property.
	 */
	public static void set( Object key, Object value) {
		globals.put(key, value);
	}

	/**
	 * Saves the properties to an output stream, using serialization.
	 * @param oo The output channel.
	 * @throws IOException If an I/O error occurs, such as a property key or value is not serializable.
	 */
	public static void saveTo(ObjectOutput oo) throws IOException {
		oo.writeObject(globals);
		oo.flush();
	}

	/**
	 * Saves the properties to a file, using serialization.
	 * @param f The file which will be written to.
	 * @throws IOException If an I/O error occurs, such as a property key or value is not serializable.
	 */
	public static void saveTo(File f) throws IOException {
		FileOutputStream fos = new FileOutputStream(f);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		saveTo(oos);
		oos.close();
		fos.close();
	}

	/**
	 * Merges the properties read from an input stream with existing ones. Duplicated entries will be replaced.
	 * @param oi The input stream from which the serialized properties will be read.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public static void mergeWith(ObjectInput oi) throws IOException, ClassNotFoundException {
		Map m = (Map) oi.readObject();
		for (Object i : m.keySet())
			globals.put(i, m.get(i));
	}

	/**
	 * Loads the properties from an input stream. Existing ones are cleared.
	 * @param oi The input stream from which the serialized properties will be read.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void loadFrom(ObjectInput oi) throws IOException, ClassNotFoundException {
		globals.clear();
		mergeWith(oi);
	}

	/**
	 * Loads the properties from a file. Existing ones are cleared.
	 * @param f The file containing the serialized properties.
	 */
	public static void loadFrom(File f) {
		try {
			FileInputStream fis = new FileInputStream(f);
			ObjectInputStream ois = new ObjectInputStream(fis);
			loadFrom(ois);
			ois.close();
			fis.close();
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	/**
	 * Dumps/Prints the list of properties to a printstream.
	 * @param ps The stream to print to.
	 */
	public static void dumpTo( PrintStream ps ) {
		for( Map.Entry<Object, Object> i : globals.entrySet() )
			ps.println( i.getKey() + " : " + i.getValue() ) ;
	}
	
	private static Map<Object, Object> globals = new HashMap<Object, Object>();
}
