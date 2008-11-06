package simsim.utils;

public class Threading {
	
	static public Thread newThread( Runnable r, boolean daemon ) {
		Thread res = new Thread( r) ;
		res.setDaemon( daemon ) ;
		return res ;
	}
	
	static public void sleep( int ms ) {
		try { Thread.sleep( ms ) ;} catch( Exception x ){};
	}

	static public void sleep( int ms, int ns ) {
		try { Thread.sleep( ms, ns ) ;} catch( Exception x ){};
	}

	static public void waitOn( Object o ) {
		synchronized( o ) {
			try { o.wait() ;} catch( Exception x ){ x.printStackTrace() ; }
		}
	}

	static public void notifyOn( Object o ) {
		synchronized( o ) {			
			o.notify() ;
		}
	}
	
	static public void notifyAllOn( Object o ) {
		synchronized( o ) {
			o.notifyAll() ;
		}
	}
}
