package simsim.ext.rmi;

/**
 * This class is used when a RMI/RPC call fails.
 * Unlike true java rmi remote exceptions this class extends RuntimeException to
 * allow for less cluttered client code, by avoiding the need for some try/catch blocks or throws clauses...
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 *
 */
@SuppressWarnings("serial")
public class RemoteException extends RuntimeException {
	public RemoteException( String m ) {
		super(m) ;
	} 
}
