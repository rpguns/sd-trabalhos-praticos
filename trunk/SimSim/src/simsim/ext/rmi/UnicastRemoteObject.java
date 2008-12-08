package simsim.ext.rmi;

import java.util.* ;
import java.lang.reflect.*;

import simsim.core.*;

/**
 * This is the basic class in the SimSim RMI/RPC package that has to be extended to implement a remote object/server.
 * See the example.
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 *
 */
public class UnicastRemoteObject implements MessageHandler {
	
	protected EndPoint endpoint;
	private Class<?>[] remotes ;
	
	@SuppressWarnings("unchecked")
	protected UnicastRemoteObject( NetAddress address ) {
		this.endpoint = address.endpoint(0, this) ;

		Set<Class> ifl = new HashSet<Class>() ;
		buildInterfaceList( getClass(), ifl ) ;
		
		HashSet<Class> rifs = new HashSet<Class>() ;
		
		for( Class i : ifl )
			if( inheritsFrom( i, Remote.class ) )
				rifs.add(i) ;
		
		remotes = rifs.toArray( new Class<?>[ rifs.size() ]) ;
	}
	
	@SuppressWarnings("unchecked")
	protected <T> T clientProxy( EndPoint client ) {
		return (T) Proxy.newProxyInstance( this.getClass().getClassLoader(), remotes, new ClientProxy( client) );
	}
		
	
	@SuppressWarnings("unchecked")
	private void buildInterfaceList( Class c, Set<Class> res ) {
		Class p = c.getSuperclass() ;
		if( p != Object.class )
			buildInterfaceList( p, res ) ;				
		res.addAll( Arrays.asList( c.getInterfaces() ) ) ;
	}
		
	@SuppressWarnings("unchecked")
	private boolean inheritsFrom( Class c, Class other) {
		if( c == other ) return true ;
		
		for( Class i : c.getInterfaces() )
			if( i == other ) return true ;
			else return inheritsFrom( i, other ) ;

		return false ;
	}
	
	class ClientProxy implements InvocationHandler {
		EndPoint client ;
		
		ClientProxy( EndPoint client ) {
			this.client = client ;
		}
		
		// Wraps the invocation data in a message and sends it to the stub.
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			RmiInvocation m = new RmiInvocation( client, method, args ) ;
			TcpChannel ch = client.tcpSend( endpoint, m) ;	
			if( ch != null ) {
				RmiReply r = ch.tcpRead() ;
				ch.close();

				if( r.reply instanceof Throwable ) // An exception occurred at the server throw it.
					throw (Throwable)r.reply ;
				else 
					return r.reply ; // The execution at the server returned correctly
			} else throw new RemoteException("Cannot connect to server...") ;
		}
	}

    //Receives the invocation details and calls the appropriate method on the stub.
    public void onReceive(TcpChannel chn, RmiInvocation m) {
            try {
                    Object res = m.method.invoke( this, m.args) ;
                    chn.tcpReply( new RmiReply( res ) ) ;
            } catch (InvocationTargetException e) {
                    chn.tcpReply(( new RmiReply( e.getCause() ))) ;
            } catch (Exception e) {
                    chn.tcpReply(( new RmiReply( e ))) ;
            }
    }

	public void onReceive(EndPoint src, Message m) {
		Thread.dumpStack() ;
	}

	public void onSendFailure(EndPoint dst, Message m) {
		Thread.dumpStack() ;
	}

	public void onReceive(TcpChannel chn, Message m) {
		Thread.dumpStack() ;
	}
	

}

