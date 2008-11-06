package simsim.core;

import java.awt.*;
import java.util.*;
import static simsim.core.Simulation.* ;

class UdpPacket extends Task {
      
    EndPoint src, dst ;
    private double delay ;
    private EncodedMessage payload ;
    
    public UdpPacket( EndPoint src, EndPoint dst, Message payload, double delay ) {
    	super( null, delay, payload.color ) ;
        this.src = src ;
        this.dst = dst ;
        this.delay = delay ;
        this.payload = payload.encode() ;        

        if( Traffic.displayLivePackets )
        	liveUdpPackets.add( this ) ;        
    }
    
    public double delay() {
        return delay ;
    }
    
    public int length() {
        return payload.length() ;
    }
    
    public void run() {
    	if( Traffic.displayLivePackets ) {
    		liveUdpPackets.remove( this ) ;
    	}
    	if( Traffic.displayDeadPackets )
    		deadUdpPackets.add( this ) ;
    	
    	if( dst.address.online ) {
            src.address.uploadedBytes += udpHeaderLength + length() ; //Includes 28 bytes of IP+UDP headers
    		dst.address.downloadedBytes += udpHeaderLength + length() ; //Includes 28 bytes of IP+UDP headers
    		payload.decode().deliverTo( src, dst.handler) ;
    	}
    }
    
    public String toString() {
        return "UdpPacket from:" + src + " to " +  dst ;
    }
    
    public void display( Graphics2D gu, Graphics2D gs) {
    	Message msg = payload.decode() ;
    	
    	if( msg.isVisible() ) {
    		double t = due - Simulation.currentTime() ;
    		double p = t / delay ;
    		msg.display( gu, gs, src, dst, t, p ) ;   	
    	}
    }
    
	static java.util.Set<UdpPacket> liveUdpPackets = new HashSet<UdpPacket>();
	static java.util.LinkedList<UdpPacket> deadUdpPackets = new LinkedList<UdpPacket>();

	private static final double udpHeaderLength = Globals.get("Net_UdpHeaderLength", 28.0 ) ;
	private static final double deadPacketHistory = Globals.get("Traffic_DeadPacketHistory", 5.0) ;
	private static final double deadPacketHistoryMaxSize = Globals.get("Traffic_DeadPacketHistoryMaxSize", 512) ;
	
	static PeriodicTask udpPacketGC = new PeriodicTask( deadPacketHistory / 5 ) {
		public void run() {
			
			double now = Simulation.currentTime() ;
			for( Iterator<UdpPacket> i = deadUdpPackets.iterator() ; i.hasNext() ; )
				if( now - i.next().due < deadPacketHistory ) break ;
				else i.remove() ;
			
			while(deadUdpPackets.size() > deadPacketHistoryMaxSize)
				deadUdpPackets.removeLast() ;

//			double now = Simulation.currentTime() ;
//			for( Iterator<UdpPacket> i = deadUdpPackets.iterator() ; i.hasNext() ; )
//				if( now - i.next().due > deadPacketHistory ) break ;
//				else i.remove() ;
//			
//			while(deadUdpPackets.size() > deadPacketHistoryMaxSize)
//				deadUdpPackets.removeLast() ;

		}
	} ;
}
